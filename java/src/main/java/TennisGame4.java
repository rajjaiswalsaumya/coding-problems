import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TennisGame4 implements TennisGame {

  int serverScore;
  int receiverScore;
  String server;
  String receiver;

  public TennisGame4(String player1, String player2) {
    this.server = player1;
    this.receiver = player2;
  }

  @java.lang.Override
  public void wonPoint(String playerName) {
    if (server.equals(playerName)) {
      this.serverScore += 1;
    } else {
      this.receiverScore += 1;
    }
  }

  @java.lang.Override
  public String getScore() {

    TennisResult result = new ResultProviderChain(this,
        Arrays.asList(GameReceiver.class, GameServer.class, AdvantageServer.class,
            AdvantageReceiver.class, DefaultResult.class, Deuce.class)).getResult();

    //TennisResult result = new Deuce(this, new GameServer(this, new GameReceiver(this, new AdvantageServer(this, new AdvantageReceiver(this, new DefaultResult(this, null)))))).getResult();
    return result.format();
  }

  boolean receiverHasAdvantage() {
    return receiverScore >= 4 && (receiverScore - serverScore) == 1;
  }

  boolean serverHasAdvantage() {
    return serverScore >= 4 && (serverScore - receiverScore) == 1;
  }

  boolean receiverHasWon() {
    return receiverScore >= 4 && (receiverScore - serverScore) >= 2;
  }

  boolean serverHasWon() {
    return serverScore >= 4 && (serverScore - receiverScore) >= 2;
  }

  boolean isDeuce() {
    return serverScore >= 3 && receiverScore >= 3 && (serverScore == receiverScore);
  }
}

class TennisResult {

  String serverScore;
  String receiverScore;

  TennisResult(String serverScore, String receiverScore) {
    this.serverScore = serverScore;
    this.receiverScore = receiverScore;
  }

  String format() {
    if ("".equals(this.receiverScore)) {
      return this.serverScore;
    }
    if (serverScore.equals(receiverScore)) {
      return serverScore + "-All";
    }
    return this.serverScore + "-" + this.receiverScore;
  }
}

interface ResultProvider {

  TennisResult getResult();
}

@Order(Integer.MAX_VALUE - 5)
class Deuce implements ResultProvider {

  private final TennisGame4 game;
  private final ResultProvider nextResult;

  public Deuce(TennisGame4 game, ResultProvider nextResult) {
    this.game = game;
    this.nextResult = nextResult;
  }

  @Override
  public TennisResult getResult() {
    if (game.isDeuce()) {
      return new TennisResult("Deuce", "");
    }
    return this.nextResult.getResult();
  }
}

@Order(Integer.MAX_VALUE - 4)
class GameServer implements ResultProvider {

  private final TennisGame4 game;
  private final ResultProvider nextResult;

  public GameServer(TennisGame4 game, ResultProvider nextResult) {
    this.game = game;
    this.nextResult = nextResult;
  }

  @Override
  public TennisResult getResult() {
    if (game.serverHasWon()) {
      return new TennisResult("Win for " + game.server, "");
    }
    return this.nextResult.getResult();
  }
}

@Order(Integer.MAX_VALUE - 3)
class GameReceiver implements ResultProvider {

  private final TennisGame4 game;
  private final ResultProvider nextResult;

  public GameReceiver(TennisGame4 game, ResultProvider nextResult) {
    this.game = game;
    this.nextResult = nextResult;
  }

  @Override
  public TennisResult getResult() {
    if (game.receiverHasWon()) {
      return new TennisResult("Win for " + game.receiver, "");
    }
    return this.nextResult.getResult();
  }
}

@Order(Integer.MAX_VALUE - 2)
class AdvantageServer implements ResultProvider {

  private final TennisGame4 game;
  private final ResultProvider nextResult;

  public AdvantageServer(TennisGame4 game, ResultProvider nextResult) {
    this.game = game;
    this.nextResult = nextResult;
  }

  @Override
  public TennisResult getResult() {
    if (game.serverHasAdvantage()) {
      return new TennisResult("Advantage " + game.server, "");
    }
    return this.nextResult.getResult();
  }
}

@Order(Integer.MAX_VALUE - 1)
class AdvantageReceiver implements ResultProvider {

  private final TennisGame4 game;
  private final ResultProvider nextResult;

  public AdvantageReceiver(TennisGame4 game, ResultProvider nextResult) {
    this.game = game;
    this.nextResult = nextResult;
  }

  @Override
  public TennisResult getResult() {
    if (game.receiverHasAdvantage()) {
      return new TennisResult("Advantage " + game.receiver, "");
    }
    return this.nextResult.getResult();
  }
}

@Order(Integer.MAX_VALUE)
class DefaultResult implements ResultProvider {

  private static final String[] scores = {"Love", "Fifteen", "Thirty", "Forty"};

  private final TennisGame4 game;

  private final ResultProvider resultProvider;

  public DefaultResult(TennisGame4 game, ResultProvider resultProvider) {
    this.game = game;
    this.resultProvider = resultProvider;
  }

  @Override
  public TennisResult getResult() {
    return new TennisResult(scores[game.serverScore], scores[game.receiverScore]);
  }
}

class ResultProviderChain {

  private final ResultProvider provider;

  public ResultProviderChain(TennisGame4 game, List<Class<? extends ResultProvider>> providers) {

    ResultProvider provider = null;

    if (providers.size() == 0) {
      throw new IllegalArgumentException("At-least one providers must be registered");
    }

    Collections.sort(providers, Comparator.comparingInt(o -> o.getAnnotation(Order.class).value()));

    try {
      for (int i = providers.size() - 1; i >= 0; i--) {
        Constructor<? extends ResultProvider> constructor = providers.get(i)
            .getDeclaredConstructor(TennisGame4.class, ResultProvider.class);
        if (i != providers.size() - 1) {
          provider = constructor.newInstance(game, provider);
        } else {
          provider = constructor.newInstance(game, null);
        }
      }
      this.provider = provider;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public TennisResult getResult() {
    return this.provider.getResult();
  }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Order {

  int value() default Integer.MAX_VALUE;
}

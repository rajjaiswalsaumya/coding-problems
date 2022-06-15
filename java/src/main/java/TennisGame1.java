public class TennisGame1 implements TennisGame {

  private int m_score1 = 0;
  private int m_score2 = 0;
  private String player1Name;
  private String player2Name;

  public TennisGame1(String player1Name, String player2Name) {
    this.player1Name = player1Name;
    this.player2Name = player2Name;
  }

  public void wonPoint(String playerName) {
    if (playerName == "player1") {
      m_score1 += 1;
    } else {
      m_score2 += 1;
    }
  }

  public String getScore() {
    String score = "";
    int tempScore = 0;
    if (m_score1 == m_score2) {
      score = equalScoresCheck(m_score1);
    } else if (m_score1 >= 4 || m_score2 >= 4) {
      int minusResult = m_score1 - m_score2;
      if (minusResult == 1) {
        score = "Advantage player1";
      } else if (minusResult == -1) {
        score = "Advantage player2";
      } else if (minusResult >= 2) {
        score = "Win for player1";
      } else {
        score = "Win for player2";
      }
    } else {
      score = getScoreValue();
    }
    return score;
  }

  private String getScoreValue() {
    String score = String.format("%s-%s", scoreToString(m_score1), scoreToString(m_score2));
    return score;
  }

  private static String scoreToString(int tempScore) {
    String temp = "";
    switch (tempScore) {
      case 0:
        temp = "Love";
        break;
      case 1:
        temp = "Fifteen";
        break;
      case 2:
        temp = "Thirty";
        break;
      case 3:
        temp = "Forty";
        break;
    }
    return temp;
  }

  private String equalScoresCheck(int score1) {
    String score;

    {
      switch (score1) {
        case 0:
        case 1:
        case 2:
          score = String.format("%s-All", scoreToString(score1));
          break;
        default:
          score = "Deuce";
          break;
      }
    }
    return score;
  }
}

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TennisGameRunner {

  public static void main(String[] args) {
    List<Integer> numbers = new ArrayList<Integer>() {{
      add(1);
      add(2);
      add(3);
    }};

    //System.out.println(numbers.stream().map(square).map(addOne).collect(Collectors.toList()));

    System.out.println(
        numbers.stream().map(e -> squareAndAddOne.apply(e)).collect(Collectors.toList()));
  }

  static Function<Integer, Integer> square = ele -> ele * ele;

  static Function<Integer, Integer> addOne = ele -> ele + 1;

  static Function<Integer, Integer> squareAndAddOne = square.andThen(addOne);

}

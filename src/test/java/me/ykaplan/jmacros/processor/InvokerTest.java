package me.ykaplan.jmacros.processor;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

public class InvokerTest {
  @Test
  public void noMethodsInClass() throws Exception {
    var clazz = NoMethodClass.class;
    var invoker = new Invoker(clazz);

    assertThatThrownBy(
            () -> {
              invoker.invoke("method", List.of());
            })
        .isInstanceOf(Exception.class);
  }

  @Test
  public void notEnoughArguments() throws Exception {
    var clazz = ClassWithMethods.class;
    var invoker = new Invoker(clazz);
    assertThatThrownBy(
            () -> {
              invoker.invoke("methodWithMoreThanOneArguments", List.of("aa"));
            })
        .isInstanceOf(Exception.class);
  }

  @Test
  public void lastArgumentInNotArray() throws Exception {
    var clazz = ClassWithMethods.class;
    var invoker = new Invoker(clazz);
    assertThatThrownBy(
            () -> {
              invoker.invoke("methodWithTwoArguments", List.of("aa", "bb", "cc"));
            })
        .isInstanceOf(Exception.class);
  }

  private static class NoMethodClass {}

  private static class ClassWithMethods {
    public static String methodWithMoreThanOneArguments(
        String hello, String world, String... others) {
      return " ";
    }

    public static String methodWithTwoArguments(String hello, String world) {
      return " ";
    }
  }
}

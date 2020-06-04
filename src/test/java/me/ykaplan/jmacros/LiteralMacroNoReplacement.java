package me.ykaplan.jmacros;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.nio.file.Paths;
import java.time.Instant;
import org.junit.jupiter.api.Test;

public class LiteralMacroNoReplacement {

  @Test
  public void buildEnvironmentVariable() {
    assertThat(LiteralMacro.buildEnvironmentVariable("ShouldBeEmpty")).isEmpty();
  }

  @Test
  public void buildTime() {
    var theBuildTime = LiteralMacro.buildTime();

    assertThat(theBuildTime)
        .isAfterOrEqualTo(Instant.ofEpochMilli(1590941286860L))
        .isBeforeOrEqualTo(Instant.now());
  }

  @Test
  public void classType() {
    assertThat(LiteralMacro.classType()).isEqualTo(LiteralMacroNoReplacement.class);
  }

  @Test
  public void className() {
    assertThat(LiteralMacro.className()).isEqualTo(LiteralMacroNoReplacement.class.getSimpleName());
  }

  @Test
  public void fileContentAsString_NoSuchFile() {
    assertThatThrownBy(() -> LiteralMacro.fileContentAsString("noSuchFile.Nop"))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  public void fileContentAsString_FileExists() {
    var classLoader = getClass().getClassLoader();
    var fileName = classLoader.getResource("test/integration/test.txt").getFile();

    var content = LiteralMacro.fileContentAsString(fileName);

    assertThat(content).isEqualTo("Hello World.");
  }

  @Test
  public void fileContentAsBytes_NoSuchFile() {
    assertThatThrownBy(() -> LiteralMacro.fileContentAsBytes("noSuchFile.Nop"))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  public void fileContentAsBytes_FileExists() {
    var classLoader = getClass().getClassLoader();
    var fileName = classLoader.getResource("test/integration/test.txt").getFile();

    var content = LiteralMacro.fileContentAsBytes(fileName);

    assertThat(content).hasSize(12);
  }

  @Test
  public void urlContentAsString_NoSuchFile() {
    assertThatThrownBy(
            () -> {
              LiteralMacro.urlContentAsString("noo://noSuchFile.Nop");
            })
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  public void urlContentAsString_FileExists() {
    var classLoader = getClass().getClassLoader();
    var fileName = classLoader.getResource("test/integration/test.txt").getFile();

    var content = LiteralMacro.urlContentAsString("file://" + fileName);

    assertThat(content).isEqualTo("Hello World.");
  }

  @Test
  public void urlContentAsBytes_NoSuchFile() {
    assertThatThrownBy(
            () -> {
              LiteralMacro.urlContentAsBytes("???://noSuchFile.Nop");
            })
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  public void urlContentAsBytes_FileExists() {
    var classLoader = getClass().getClassLoader();
    var fileName = classLoader.getResource("test/integration/test.txt").getFile();

    var content = LiteralMacro.urlContentAsBytes("file://" + fileName);

    assertThat(content).hasSize(12);
  }

  @Test
  public void executeScript() throws Exception {
    var uri =
        getClass().getClassLoader().getResource("test/integration/ExecuteScript/test1.sh").toURI();

    var path = Paths.get(uri);
    assertThat(LiteralMacro.executeScript(path.toAbsolutePath().toString()))
        .isEqualTo("this is echo\n");
  }

  @Test
  public void fileName() {
    assertThat(LiteralMacro.fileName()).endsWith("LiteralMacroNoReplacement.java");
  }

  @Test
  public void lineNumber() {
    assertThat(LiteralMacro.lineNumber()).isBetween(0, 10000);
  }

  @Test
  public void methodName_test() {
    assertThat(LiteralMacro.methodName()).isEqualTo("methodName_test");
  }

  @Test
  public void javaScript_returnString() {
    var script = "'h' + 'i'";

    var result = LiteralMacro.javaScript(script);

    assertThat(result).isEqualTo("hi");
  }

  @Test
  public void javaScript_returnBoolean() {
    var script = "'1' == 1";

    var result = LiteralMacro.javaScript(script);

    assertThat(result).isEqualTo(true);
  }

  @Test
  public void javaScript_returnNumber() {
    var script = "1 + 10 * 2";

    var result = LiteralMacro.javaScript(script);

    assertThat(result).isEqualTo(21);
  }

  @Test
  public void javaScript_returnNull() {
    var script = "var obj = {a:1, b:2, c:null}; obj.c";

    var result = LiteralMacro.javaScript(script);

    assertThat(result).isNull();
  }

  @Test
  public void javaScript_returnJson() {
    var script = "var obj = {a:1, b:2, c:null, d: []}; obj.d";

    var result = LiteralMacro.javaScript(script);

    assertThat(result).isEqualTo("[]");
  }

  @Test
  public void javaScript_returnUnknown() {
    var script = "Java.type('java.util.Map');";
    assertThatThrownBy(() -> LiteralMacro.javaScript(script))
        .hasMessageContaining("Can not change ");
  }

  @Test
  public void javaScript_invalidScript() {
    var script = "{[";
    assertThatThrownBy(() -> LiteralMacro.javaScript(script)).isInstanceOf(RuntimeException.class);
  }
}

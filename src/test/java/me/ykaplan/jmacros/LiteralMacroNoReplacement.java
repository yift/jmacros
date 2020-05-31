package me.ykaplan.jmacros;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.nio.file.Paths;
import java.time.Instant;
import org.junit.jupiter.api.Test;

public class LiteralMacroNoReplacement {
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
  public void executeScript() throws Exception {
    var uri =
        getClass().getClassLoader().getResource("test/integration/ExecuteScript/test1.sh").toURI();

    var path = Paths.get(uri);
    assertThat(LiteralMacro.executeScript(path.toAbsolutePath().toString()))
        .isEqualTo("this is echo\n");
  }
}

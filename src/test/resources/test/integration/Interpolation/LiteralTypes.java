import me.ykaplan.jmacros.Interpolation;

public class LiteralTypes {
    @Interpolation
    public static String go() {
        var shrt = "`(short)1`";
        var chr = "`'A'`";
        var byt = "`(byte)10`";
        var flt = "`5.3F`";
        var bool = "`true`";
        var bool2 = "`false`";
        var nil = "`null`";

        return shrt + chr + byt + flt + bool + bool2 + nil;
    }
}
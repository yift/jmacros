import me.ykaplan.jmacros.Alias;

public class ListOfAliases {
    private final int i;
    private final int j;
    private final String k;

    @Alias({"me as this", "self as this"})
    private ListOfAliases() {
        me.i = 12;
        me.j = 33;
        self.k = " -- ";
    }


    @Alias({"me as this", "self as this", "Os as java.util.Objects"})
    private String doIt() {
        return Os.toString(self.k ) + Os.hash(j, me.k);
    }

    public static String go() {
        return new ListOfAliases().doIt();
    }
}

package me.ykaplan.jmacros;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This is a helper annotation. Use this to print (to the standard output) the structure of the
 * javac tree of an element.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface DebugPrint {}

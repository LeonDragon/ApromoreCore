/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.similaritysearch.common;

import org.apromore.similaritysearch.common.stemmer.SnowballStemmer;

import java.util.StringTokenizer;

import static java.lang.Class.forName;

public class Settings {
    public static String STRING_DELIMETER = " ,.:;&/?!#()";

    public static boolean logResult = true;
    public static boolean considerEvents = true;
    public static boolean considerGateways = true;

    private static SnowballStemmer englishStemmer;

    public static SnowballStemmer getEnglishStemmer() {
        if (englishStemmer == null) {
            englishStemmer = getStemmer("english");
        }

        return englishStemmer;
    }

    @SuppressWarnings("rawtypes")
    public static SnowballStemmer getStemmer(String language) {
        Class stemClass;
        SnowballStemmer stemmer;

        try {
            stemClass = forName("org.apromore.similaritysearch.common.stemmer.ext." + language + "Stemmer");
            stemmer = (SnowballStemmer) stemClass.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return stemmer;
    }

    public static String removeSpaces(String s) {
        StringTokenizer st = new StringTokenizer(s);
        String result = "";

        while (st.hasMoreTokens()) {
            result += st.nextToken() + (st.hasMoreTokens() ? " " : "");
        }

        return result;
    }
}

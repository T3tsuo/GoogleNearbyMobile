package com.example.googlenearbymobile.LocationSharingLibJava.src;

import java.util.ArrayList;
import java.util.List;

public class ParseString {
    private int unprocessedIndex;

    public ParseString() {}

    public List<Object> parseString(String list) {
        unprocessedIndex = 0;

        String[] splittedList = list.split(",");

        // remove first character [
        splittedList[0] = splittedList[0].substring(1);

        List<Object> results = parseStringHelper(splittedList, new ArrayList<>());
        System.out.println(results);
        return results;
    }

    private ArrayList<Object> parseStringHelper(String[] splittedList, ArrayList<Object> newList) {

        while (unprocessedIndex < splittedList.length) {
            if (splittedList[unprocessedIndex].charAt(0) == '[') {
                // create new list
                splittedList[unprocessedIndex] = splittedList[unprocessedIndex].substring(1);
                newList.add(parseStringHelper(splittedList, new ArrayList<>()));
            }
            else if (splittedList[unprocessedIndex].contains("]")) {
                // general exit and check if last item exists more than one nested list
                boolean shouldExit = checkEndStatement(splittedList[unprocessedIndex]);
                // if multiple exits
                if (shouldExit) {
                    splittedList[unprocessedIndex] = splittedList[unprocessedIndex].substring(0, splittedList[unprocessedIndex].length() - 1);
                    if (splittedList[unprocessedIndex].equals("")) {
                        unprocessedIndex++;
                    }
                    return newList;
                }
                splittedList[unprocessedIndex] = splittedList[unprocessedIndex].substring(0, splittedList[unprocessedIndex].length() - 1);
                String nested = "";
                while (splittedList[unprocessedIndex].contains("]")) {
                    splittedList[unprocessedIndex] = splittedList[unprocessedIndex].substring(0, splittedList[unprocessedIndex].length() - 1);
                    nested = nested + "]";
                }
                newList.add(splittedList[unprocessedIndex]);
                splittedList[unprocessedIndex] = nested;
                if (nested.equals("")) {
                    unprocessedIndex++;
                }
                return newList;
            }
            else if (splittedList[unprocessedIndex].charAt(0) != '[' && !splittedList[unprocessedIndex].contains("]")) {
                // regular item in a list
                newList.add(splittedList[unprocessedIndex]);
                unprocessedIndex++;
            }
        }

        return newList;
    }

    private boolean checkEndStatement(String check) {
        if (check.equals("")) {
            return false;
        }
        for (int i = 0; i < check.length(); i++) {
            if (check.charAt(i) != ']') {
                return false;
            }
        }
        return true;
    }

    public String grabInnerData(List<Object> results, ArrayList<Integer> indexes) {
        for (int i = 0; i < indexes.size(); i++) {
            // if it's still a list
            if (results.get(indexes.get(i)).toString().contains("[")) {
                results = (List<Object>) results.get(indexes.get(i));
            } else {
                return results.get(indexes.get(i)).toString();
            }
        }
        return "";
    }

    public List<Object> grabInnerList(List<Object> results, ArrayList<Integer> indexes) {
        for (int i = 0; i < indexes.size(); i++) {
            results = (List<Object>) results.get(indexes.get(i));
        }
        return results;
    }

}

package com.example.collegeinfo;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.ArrayList;
import java.util.List;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbusiveWordFilter implements InputFilter {

    private List<String> abusiveWords;

    public AbusiveWordFilter() {
        // Initialize the list of abusive words
        abusiveWords = new ArrayList<>(Arrays.asList("BC", "MC", "MKC"));
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // Convert the input to lowercase for case-insensitive matching
        String input = source.subSequence(start, end).toString().toLowerCase();

        // Check if the input contains any abusive words
        for (String word : abusiveWords) {
            if (input.contains(word)) {
                // If an abusive word is found, replace it with asterisks (*) of the same length
                StringBuilder filtered = new StringBuilder();
                for (int i = start; i < end; i++) {
                    filtered.append("*");
                }
                return filtered.toString();
            }
        }

        // If no abusive words are found, return null to accept the input as is
        return null;
    }
}

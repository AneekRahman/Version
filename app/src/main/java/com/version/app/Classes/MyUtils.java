package com.version.app.Classes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class MyUtils {

    public MyUtils(){

    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String formatNumbers(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatNumbers(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatNumbers(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavBarHeight(Context context){

        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;

    }

    public static CharSequence getTrimmedText(final CharSequence originalText, int trimLength, String ellipsis, String extraText) {
        if (originalText != null && originalText.length() > trimLength) {

            SpannableStringBuilder longDescription = new SpannableStringBuilder(originalText, 0 , trimLength + 1);
            int start = longDescription.length();
            longDescription.append(ellipsis + extraText);
            longDescription.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, longDescription.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            longDescription.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {

                    Toast.makeText(widget.getContext(), " <<<<<<<< SEE MORE CLICKED!! >>>>>>>>", Toast.LENGTH_SHORT).show();

                }
            }, start + ellipsis.length(), longDescription.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return longDescription;


        } else {
            return originalText;
        }
    }

    public static void transpStatusBar(Context context){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = ((Activity) context).getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

    }

    // Get the percentage of visibility of a home post
    public static ArrayList<Integer> getPercentage(RecyclerView recyclerView, LinearLayoutManager layoutManager){

        ArrayList<Integer> arrayList = new ArrayList<Integer>();

        try {

            final int firstPosition = layoutManager.findFirstVisibleItemPosition();
            final int lastPosition = layoutManager.findLastVisibleItemPosition();

            Rect rvRect = new Rect();
            recyclerView.getGlobalVisibleRect(rvRect);

            for (int i = firstPosition; i <= lastPosition; i++) {
                Rect rowRect = new Rect();
                layoutManager.findViewByPosition(i).getGlobalVisibleRect(rowRect);

                int percentFirst;
                if (rowRect.right >= rvRect.right){
                    int visibleHeightFirst =rvRect.right - rowRect.left;
                    percentFirst = (visibleHeightFirst * 100) / layoutManager.findViewByPosition(i).getHeight();
                }else {
                    int visibleHeightFirst = rowRect.right - rvRect.left;
                    percentFirst = (visibleHeightFirst * 100) / layoutManager.findViewByPosition(i).getHeight();
                }

                if (percentFirst>100)
                    percentFirst = 100;


                arrayList.add(percentFirst);
            }

        }finally {

            return arrayList;

        }

    }

}

/*
 * WarpPath.java   Jul 14, 2004
 *
 * Copyright (c) 2004 Stan Salvador
 * stansalvador@hotmail.com
 */

package de.qaware.chronix.dtw;


import de.qaware.chronix.dt.IntList;
import de.qaware.chronix.matrix.ColMajorCell;

import java.util.NoSuchElementException;

public class WarpPath {
    // DATA
    private IntList tsIindexes;   // ArrayList of Integer
    private IntList tsJindexes;   // ArrayList of Integer

    // CONSTRUCTORS
    public WarpPath() {
        tsIindexes = new IntList();
        tsJindexes = new IntList();
    }


    public WarpPath(int initialCapacity) {
        tsIindexes = new IntList(initialCapacity);
        tsJindexes = new IntList(initialCapacity);
    }


    // FUNCTIONS
    public int size() {
        return tsIindexes.size();
    }


    public int minI() {
        return tsIindexes.get(tsIindexes.size() - 1);
    }


    public int minJ() {
        return tsJindexes.get(tsJindexes.size() - 1);
    }

    public int maxI() {
        return tsIindexes.get(0);
    }


    public int maxJ() {
        return tsJindexes.get(0);
    }


    boolean shouldBeInverted = false;

    public void addFirst(int i, int j) {
        //We have to revert the index. That saves time.
        //tsIindexes.add(0,i);
        //tsJindexes.add(0,i);
        tsIindexes.add(i);
        tsJindexes.add(j);
        shouldBeInverted = true;
    }


    private void revert() {
       if (shouldBeInverted) {
           IntList revertedI = new IntList(tsIindexes.size());
           IntList revertedJ = new IntList(tsJindexes.size());

           for (int i = tsIindexes.size() - 1; i >= 0; i--) {
               revertedI.add(tsIindexes.get(i));
               revertedJ.add(tsJindexes.get(i));
           }

           tsIindexes = revertedI;
           tsJindexes = revertedJ;
           shouldBeInverted = false;
       }
    }


    public void addLast(int i, int j) {
        tsIindexes.add(0, i);
        tsJindexes.add(0, j);
    }


    public IntList getMatchingIndexesForI(int i) {
        int index = tsIindexes.indexOf(i);
        if (index < 0)
            throw new InternalError("ERROR:  index '" + i + " is not in the " + "warp path.");
        final IntList matchingJs = new IntList(tsIindexes.size());
        while (index < tsIindexes.size() && tsIindexes.get(index) == i)
            matchingJs.add(tsJindexes.get(index++));

        return matchingJs;
    }


    public IntList getMatchingIndexesForJ(int j) {
        int index = tsJindexes.indexOf(j);
        if (index < 0)
            throw new InternalError("ERROR:  index '" + j + " is not in the " + "warp path.");
        final IntList matchingIs = new IntList(tsJindexes.size());
        while (index < tsJindexes.size() && tsJindexes.get(index) == j)
            matchingIs.add(tsIindexes.get(index++));

        return matchingIs;
    }


    // Create a new WarpPath that is the same as THIS WarpPath, but J is the reference template, rather than I.
    public WarpPath invertedCopy() {
        final WarpPath newWarpPath = new WarpPath();
        for (int x = 0; x < tsIindexes.size(); x++)
            newWarpPath.addLast(tsJindexes.get(x), tsIindexes.get(x));

        return newWarpPath;
    }


    // Swap I and J so that the warp path now indicates that J is the template rather than I.
    public void invert() {
        for (int x = 0; x < tsIindexes.size(); x++) {
            final int temp = tsIindexes.get(x);
            tsIindexes.set(x, tsJindexes.get(x));
            tsJindexes.set(x, temp);
        }
    }  // end invert()


    public ColMajorCell get(int index) {
        if ((index > this.size()) || (index < 0))
            throw new NoSuchElementException();
        else
            return new ColMajorCell(tsIindexes.get(tsIindexes.size() - index - 1), tsJindexes.get(tsJindexes.size() - index - 1));
    }


    public String toString() {
        StringBuilder outStr = new StringBuilder("[");
        for (int x = 0; x < tsIindexes.size(); x++) {
            outStr.append("(").append(tsIindexes.get(x)).append(",").append(tsJindexes.get(x)).append(")");
            if (x < tsIindexes.size() - 1)
                outStr.append(",");
        }
        outStr.append("]");

        return outStr.toString();
    }  // end toString()


    public boolean equals(Object obj) {
        if ((obj instanceof WarpPath))  // trivial false test
        {
            final WarpPath p = (WarpPath) obj;
            if ((p.size() == this.size()) && (p.maxI() == this.maxI()) && (p.maxJ() == this.maxJ())) // less trivial reject
            {
                // Compare each value in the warp path for equality
                for (int x = 0; x < this.size(); x++)
                    if (!(this.get(x).equals(p.get(x))))
                        return false;

                return true;
            } else
                return false;
        } else
            return false;
    }  // end equals


    public int hashCode() {
        return tsIindexes.hashCode() * tsJindexes.hashCode();
    }

}  // end class WarpPath
package com.whxm.harbor.graph;

/**
 * @author : Ftibw
 * @date : 2018/11/30 15:23
 */
public interface Weight<W> {

    W add(W otherOne);

    int compareTo(W otherOne);
}

package com.whxm.harbor.graph;

import com.whxm.harbor.exception.DataNotFoundException;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @param <ID> 顶点ID
 * @param <V>  顶点对象:vertex
 * @param <E>  边对象:edge
 * @param <W>  边权单位
 * @author : Ftibw
 * @date : 2018/11/29 13:44
 */
public class PathFinder<ID, V, E, W extends Weight<W>> {
    private static final String BEELINE_POINT = "path";
    private static final String BEELINE_WEIGHT = "weight";
    /**
     * 顶点集合
     */
    private Map<ID, V> vertices;
    /**
     * 邻接边表
     */
    private Map<ID, List<E>> adjacencyTable;

//  private ID startId;
    /**
     * 终点ID
     */
    private ID endId;
    /**
     * 终点
     */
    private V endVertex;
    /**
     * 当前点到终点的最短路线边权值总和估值的获取算法,由外部提供
     */
    private BiFunction<V, V, W> getterOfH;
    /**
     * 包装点表
     * 作用---避免 loop test if point in openList
     */
    private Map<ID, Wrapper> wrapperTable = new HashMap<>();
    /**
     * 待检索点集合
     */
    private PriorityQueue<Wrapper> openList = new PriorityQueue<>((wrapper1, wrapper2) -> wrapper1.f.compareTo(wrapper2.f));

    private final Wrapper prototype = new Wrapper();

    private class Wrapper {
        /**
         * 图中的顶点
         */
        ID pointId;
        /**
         * 起点到终点最短路径边权值总和的估算值
         */
        W f;
        /**
         * 起点到当前点已确认的最短路径边权值之和
         */
        W g;
        /**
         * 当前点到终点的最短路线边权值总和估值
         */
        W h;
        /**
         * 当前点是否已经检索过
         */
        Boolean closed;
        /**
         * 当前点的前继
         */
        Wrapper previous;
    }

    /**
     * 构造寻路器
     *
     * @param vertices       路径点集合
     * @param adjacencyTable 路径点邻接出边表
     * @param startId        起点ID
     * @param endId          终点ID
     * @param hGetter        {@link BiFunction}泛型<当前点对象,终点对象,两点哈密顿值>
     *                       推荐: H = Math.abs(end.x - point.x) + Math.abs(end.y - point.y)
     */
    public PathFinder(Map<ID, V> vertices,
                      Map<ID, List<E>> adjacencyTable,
                      ID startId,
                      ID endId,
                      W zeroWeight,
                      BiFunction<V, V, W> hGetter) {
        this.vertices = vertices;
        this.adjacencyTable = adjacencyTable;
        this.endId = endId;
        this.getterOfH = hGetter;
        if (null == vertices.get(startId))
            throw new DataNotFoundException("起点数据错误,请重新输入");

        this.endVertex = vertices.get(endId);
        if (null == this.endVertex)
            throw new DataNotFoundException("终点数据错误,请重新输入");
        Wrapper wrappedStart = wrapPoint(startId, zeroWeight);
        wrappedStart.previous = prototype;//prototype仅做占位用
        this.openList.offer(wrappedStart);
    }

    /**
     * 包装点
     *
     * @param pointId 当前点ID
     * @param g       当前点到起点的最短路径
     */
    private Wrapper wrapPoint(ID pointId, W g) {
        Map<ID, V> vertices = this.vertices;
        Wrapper wrapper = new Wrapper();
        wrapper.pointId = pointId;
        wrapper.g = g;
        V curV = vertices.get(pointId);
        if (null == curV)
            throw new DataNotFoundException("当前边中tail=" + pointId + "的头点不在顶点集中");
        wrapper.h = this.getterOfH.apply(curV, this.endVertex);
        wrapper.f = g.add(wrapper.h);
        this.wrapperTable.put(pointId, wrapper);
        return wrapper;
    }

    public Map<String, Object> findPath(Function<E, ID> edgeHeadGetter,
                                        Function<E, W> edgeWeightGetter) {
        Map<ID, Wrapper> wrapperTable = this.wrapperTable;
        PriorityQueue<Wrapper> openList = this.openList;
        ID endId = this.endId;
        do {
            //取出f最小的点并出列
            Wrapper current = openList.poll();
            if (null == current)
                break;
            current.closed = Boolean.TRUE;
            if (current.pointId.equals(endId))
                break;
            List<E> adjacencyEdges = this.adjacencyTable.get(current.pointId);
            if (null == adjacencyEdges)
                continue;
            for (E edge : adjacencyEdges) {
                ID nextPointId = edgeHeadGetter.apply(edge);
                Wrapper next = wrapperTable.get(nextPointId);
                W nextG = current.g.add(edgeWeightGetter.apply(edge));
                if (null == next) {
                    next = wrapPoint(nextPointId, nextG);
                    next.previous = current;
                    openList.offer(next);
                } else if (nextG.compareTo(next.g) < 0) {
                    next.previous = current;
                    next.g = nextG;
                    next.f = nextG.add(next.h);
                }
            }
        } while (openList.size() > 0);
        Wrapper wrappedEnd = wrapperTable.get(endId);
        if (null == wrappedEnd) return null;
        Map<String, Object> ret = new HashMap<>();
        ret.put(BEELINE_WEIGHT, wrappedEnd.f);

        List<V> path = new ArrayList<>();
        //wrappedStart.previous需要设置一个占位(目前用prototype占位),
        //否则wrappedStart不会被加入path
        while (null != wrappedEnd.previous) {
            path.add(0, vertices.get(wrappedEnd.pointId));
            wrappedEnd = wrappedEnd.previous;
        }
        ret.put(BEELINE_POINT, path);
        return ret;
    }
}

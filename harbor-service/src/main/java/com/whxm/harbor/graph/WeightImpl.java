package com.whxm.harbor.graph;


/**
 * @author : Ftibw
 * @date : 2018/11/30 16:02
 */
public class WeightImpl implements Weight<WeightImpl>, Cloneable {

    private static final WeightImpl prototype = new WeightImpl();

    //private Double ratio;//used for COMPARE_BY_DISTANCE_AND_TIME,调控时间/距离的计算占比

    private Double distance;

    private Double time;

    private ECompareMode compareMode = ECompareMode.COMPARE_BY_DISTANCE;

    @Override
    public WeightImpl add(WeightImpl otherOne) {
        return WeightImpl.newInstance(this.distance + otherOne.distance,
                this.time + otherOne.time);
    }

    @Override
    public int compareTo(WeightImpl otherOne) {
        double offset;
        switch (this.compareMode) {
            case COMPARE_BY_DISTANCE:
                offset = this.distance - otherOne.distance;
                break;
            case COMPARE_BY_TIME:
                offset = this.time - otherOne.time;
                break;
            case COMPARE_BY_DISTANCE_AND_TIME:
                offset = (this.distance - otherOne.distance) / 2 + (this.time - otherOne.time) / 2;
                break;
            default:
                throw new RuntimeException("Weight compared failed");
        }
        return offset > 0 ? 1 : offset < 0 ? -1 : 0;
    }


    public static WeightImpl newInstance(Double distance, Double time) {
        try {
            WeightImpl clone = (WeightImpl) WeightImpl.prototype.clone();
            clone.setDistance(distance);
            clone.setTime(time);
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException("WeightImpl.prototype clone failed");
        }
    }

    public enum ECompareMode {
        COMPARE_BY_DISTANCE("distance"),
        COMPARE_BY_TIME("time"),
        COMPARE_BY_DISTANCE_AND_TIME("distanceAndTime");
        private String name;

        ECompareMode(String name) {
            this.name = name;
        }
    }

    public void setComparator(ECompareMode compareMode) {
        this.compareMode = compareMode;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public String getCompareMode() {
        return this.compareMode.name;
    }
}

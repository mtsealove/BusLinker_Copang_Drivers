package mtsealove.com.github.BuslinkerDrivers.Entity;

public class RunInfo {
    String startAddr;
    String startTime;
    String endAddr;
    String endTime;
    int wayloadCnt;
    int cost;
    int ID;

    public RunInfo(String startAddr, String startTime, String endAddr, String endTime, int wayloadCnt, int cost, int id){
        this.startAddr=startAddr;
        this.startTime=startTime;
        this.endAddr=endAddr;
        this.endTime=endTime;
        this.wayloadCnt=wayloadCnt;
        this.cost=cost;
        this.ID=id;
    }

    public String getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(String startAddr) {
        this.startAddr = startAddr;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndAddr() {
        return endAddr;
    }

    public void setEndAddr(String endAddr) {
        this.endAddr = endAddr;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getWayloadCnt() {
        return wayloadCnt;
    }

    public void setWayloadCnt(int wayloadCnt) {
        this.wayloadCnt = wayloadCnt;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}

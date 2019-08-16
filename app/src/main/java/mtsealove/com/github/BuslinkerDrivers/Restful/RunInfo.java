package mtsealove.com.github.BuslinkerDrivers.Restful;

import org.json.JSONObject;

import java.io.Serializable;

public class RunInfo implements Serializable {
    int ID, charge, wayloadCnt;
    String startAddr,
    startTime,
    endAddr,
    endTime,
    wayloadAddrs,
    wayloadCats,
    nickname,
    Company,
    ContractStart,
    ContractEnd,
    startName,
    endName,
    wayloadNames,
    RunDate;

    public RunInfo(int ID, int charge, String startAddr, String startTime, String endAddr, String endTime, String wayloadAddrs, String wayloadCats, String nickname, String company, String contractStart, String contractEnd, String startName, String endName, String wayloadNames, String runDate) {
        this.ID = ID;
        this.charge = charge;
        this.startAddr = startAddr;
        this.startTime = startTime;
        this.endAddr = endAddr;
        this.endTime = endTime;
        this.wayloadAddrs = wayloadAddrs;
        this.wayloadCats = wayloadCats;
        this.nickname = nickname;
        Company = company;
        ContractStart = contractStart;
        ContractEnd = contractEnd;
        this.startName = startName;
        this.endName = endName;
        this.wayloadNames = wayloadNames;
        RunDate = runDate.substring(0, 10);
        wayloadCnt=wayloadAddrs.split(";;").length;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
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

    public String getWayloadAddrs() {
        return wayloadAddrs;
    }

    public void setWayloadAddrs(String wayloadAddrs) {
        this.wayloadAddrs = wayloadAddrs;
    }

    public String getWayloadCats() {
        return wayloadCats;
    }

    public void setWayloadCats(String wayloadCats) {
        this.wayloadCats = wayloadCats;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getContractStart() {
        return ContractStart;
    }

    public void setContractStart(String contractStart) {
        ContractStart = contractStart;
    }

    public String getContractEnd() {
        return ContractEnd;
    }

    public void setContractEnd(String contractEnd) {
        ContractEnd = contractEnd;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public String getWayloadNames() {
        return wayloadNames;
    }

    public void setWayloadNames(String wayloadNames) {
        this.wayloadNames = wayloadNames;
    }

    public int getWayloadCnt() {
        return wayloadCnt;
    }

    public String getRunDate() {
        return RunDate;
    }

    public void setRunDate(String runDate) {
        RunDate = runDate.substring(0, 10);
    }

    public void setWayloadCnt(int wayloadCnt) {
        this.wayloadCnt = wayloadCnt;
    }

    @Override
    public String toString() {
        return "RunInfo{" +
                "ID=" + ID +
                ", charge=" + charge +
                ", wayloadCnt=" + wayloadCnt +
                ", startAddr='" + startAddr + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endAddr='" + endAddr + '\'' +
                ", endTime='" + endTime + '\'' +
                ", wayloadAddrs='" + wayloadAddrs + '\'' +
                ", wayloadCats='" + wayloadCats + '\'' +
                ", nickname='" + nickname + '\'' +
                ", Company='" + Company + '\'' +
                ", ContractStart='" + ContractStart + '\'' +
                ", ContractEnd='" + ContractEnd + '\'' +
                ", startName='" + startName + '\'' +
                ", endName='" + endName + '\'' +
                ", wayloadNames='" + wayloadNames + '\'' +
                ", RunDate='" + RunDate + '\'' +
                '}';
    }
}

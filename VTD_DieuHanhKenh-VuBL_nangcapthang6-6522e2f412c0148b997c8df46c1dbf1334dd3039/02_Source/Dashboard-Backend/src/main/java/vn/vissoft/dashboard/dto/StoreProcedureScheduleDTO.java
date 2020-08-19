package vn.vissoft.dashboard.dto;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Objects;

public class StoreProcedureScheduleDTO {
    private Integer shceduleId;
    private String parameter;
    private String sqlCommand;
    private String logDir;
    private Long delayTime;
    private String threadName;
    private String startUpType;
    private String scheduleType;
    private Integer executionTime;
    private String additionValue;
    private Time startTime;
    private Time endTime;
    private Date expectedDate;
    private String weekDay;
    private String monthDay;
    private String yearMonth;
    private Integer executionCount;
    private String status;
    private String flag;
    private Timestamp executionDate;

    public Integer getShceduleId() {
        return shceduleId;
    }

    public void setShceduleId(Integer shceduleId) {
        this.shceduleId = shceduleId;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getSqlCommand() {
        return sqlCommand;
    }

    public void setSqlCommand(String sqlCommand) {
        this.sqlCommand = sqlCommand;
    }

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public Long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Long delayTime) {
        this.delayTime = delayTime;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getStartUpType() {
        return startUpType;
    }

    public void setStartUpType(String startUpType) {
        this.startUpType = startUpType;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Integer getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
    }

    public String getAdditionValue() {
        return additionValue;
    }

    public void setAdditionValue(String additionValue) {
        this.additionValue = additionValue;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Date getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(Date expectedDate) {
        this.expectedDate = expectedDate;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getMonthDay() {
        return monthDay;
    }

    public void setMonthDay(String monthDay) {
        this.monthDay = monthDay;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public Integer getExecutionCount() {
        return executionCount;
    }

    public void setExecutionCount(Integer executionCount) {
        this.executionCount = executionCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Timestamp getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Timestamp executionDate) {
        this.executionDate = executionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreProcedureScheduleDTO that = (StoreProcedureScheduleDTO) o;
        return Objects.equals(parameter, that.parameter) &&
                Objects.equals(sqlCommand, that.sqlCommand) &&
                Objects.equals(logDir, that.logDir) &&
                Objects.equals(delayTime, that.delayTime) &&
                Objects.equals(threadName, that.threadName) &&
                Objects.equals(startUpType, that.startUpType) &&
                Objects.equals(scheduleType, that.scheduleType) &&
                Objects.equals(executionTime, that.executionTime) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(expectedDate, that.expectedDate) &&
                Objects.equals(weekDay, that.weekDay) &&
                Objects.equals(monthDay, that.monthDay) &&
                Objects.equals(flag, that.flag) &&
                Objects.equals(executionCount, that.executionCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameter, sqlCommand, logDir, delayTime, threadName, startUpType, scheduleType, executionTime, startTime, endTime, expectedDate, weekDay, monthDay,flag, executionCount);
    }
}

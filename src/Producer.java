/**
 * Created by skwow on 11/6/2016.
 * Saurabh Kumar 2015088
 * Vanshit Gupta 2015186
 */
public interface Producer
{
    public void readData();
    public void addConsumer(Consumer p);
    public void printResult();
    public boolean signal();
    public void receiveMinData(float _minTemp,float _minHumidity,float _minRainfall);
    public void receiveMaxData(float _maxTemp,float _maxHumidity,float _maxRainfall);
    public void receiveAvData(float _avTemp,float _avHumidity,float _avRainfall);
    public void receiveNextData(float _nextTemp,float _nextHumidity,float _nextRainfall);


}

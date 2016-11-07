import java.util.ArrayList;

/**
 * Created by skwow on 11/6/2016.
 * Saurabh Kumar 2015088
 * Vanshit Gupta 2015186
 */
public interface Consumer
{
    public void receiveTempData(float[] temp);
    public void receiveHumidityData(float[] humidity);
    public void receiveRainfallData(float[] rainfall);
    public boolean signal();
    public void dostuff();
    public void addProducer(Producer p);



}

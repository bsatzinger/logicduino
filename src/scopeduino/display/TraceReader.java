/*Copyright (C) 2009,2010 Brian Satzinger

This file is part of Scopeduino.

    Scopeduino is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Scopeduino is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Scopeduino.  If not, see <http://www.gnu.org/licenses/>.
*/

package scopeduino.display;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import serial.LogicDAQ;
/**
 *
 * @author brian
 */
public class TraceReader extends Thread {

    Vector<Trace> traces;
    LogicDAQ arduino;

    ConcurrentLinkedQueue<byte[]> commandQueue;

    public TraceReader(Vector<Trace> t, LogicDAQ a)
    {
        traces = t;
        arduino = a;

        commandQueue = new ConcurrentLinkedQueue<byte[]>();
    }

    public void run()
    {
        //Used to calculate the trace rate
        int ntraces = 0;
        long time = System.currentTimeMillis();

        while (arduino.connected)
        {
            int[] data = arduino.readTrace();

            for (int i = 0; i < data.length; i++)
            {
                Integer b = new Integer(data[i]);
                System.out.println(b.toString());
            }


            //Create 8 traces, one for each bit
            Vector<Trace> tBuffer = new Vector<Trace>(8);

            int mask = 1;

            for (int b = 0; b < 7; b++)
            {
                int[] binaryString = new int[data.length];

                for (int i = 0; i < data.length; i++)
                {
                    if((mask & data[i]) == 0)
                    {
                        binaryString[i] = 0;
                    }
                    else
                    {
                        binaryString[i] = 1;
                    }
                }

                //Shift the mask to the left 1 bit
                mask = mask << 1;

                double[] dispData = new double[data.length];

                //Calculate the display coordinates for this trace
                for (int i = 0; i < data.length; i++)
                {
                    dispData[i] = b / 4.0 + binaryString[i] / 8.0 - 1;
                }

                //Create a trace with this data
                Trace t = new Trace(dispData,0.2f, 0.2f, 1.0f, 0.25f,1);

                //Add the trace to the buffer
                tBuffer.add(t);
            }

            //Add them to traces
            synchronized(traces)
            {
                //Empty the buffer
                traces.clear();

                for (Trace tr : tBuffer)
                {
                    traces.add(tr);
                }
            }

            try
            {
                this.sleep(1);
            }
            catch (Exception e)
            {
                System.err.println(e);
            }

        }
        System.err.println("TraceReader terminated because arduino disconnected");
    }

}

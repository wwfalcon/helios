/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2007, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package org.helios.ot.trace.types.interval;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.helios.ot.trace.types.LongTraceValue;
import org.helios.ot.trace.types.TraceValueType;
import org.helios.ot.type.MetricType;

//import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>Title: LongIntervalTraceValue</p>
 * <p>Description: Trace value for an interval of long traces</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * @version $LastChangedRevision$
 * <p><code>org.helios.ot.trace.types.interval.LongIntervalTraceValue</code></p>
 */
//@XStreamAlias("LongIntervalTraceValue")
public class LongIntervalTraceValue<T extends LongTraceValue> extends AbstractNumericIntervalTraceValue<T> implements IMinMaxAvgIntervalTraceValue {
	/** The minimum value for the interval */
	//@XStreamAlias("min")
	protected long min = 0;
	/** The maximum value for the interval */
	//@XStreamAlias("max")
	protected long max = 0;
	
	/**
	 * Copy Constructor
	 * @param longIntervalTraceValue a <code>LongIntervalTraceValue</code> object
	 */
	protected LongIntervalTraceValue(LongIntervalTraceValue<T> longIntervalTraceValue) {
		super(longIntervalTraceValue);
	    this.min = longIntervalTraceValue.min;
	    this.max = longIntervalTraceValue.max;
	}
	
	/**
	 * Creates a new LongIntervalTraceValue
	 * @param traces The initial traces to apply
	 */
	public LongIntervalTraceValue(T...traces) {
		super(TraceValueType.INTERVAL_LONG_TYPE);
		if(traces!=null) {
			for(T trace: traces) {
				apply(trace);
			}
		}		
	}	
	
	
	/**
	 * Clones the state of this interval trace value and then resets it's state for the next interval
	 * @param metricType The metric type of the owning trace passed so that the interval value 
	 * can execute the reset with the correct semantics.
	 * @return A clone of this object prior to reset.
	 */
	public LongIntervalTraceValue<T> cloneReset(MetricType metricType) {
		if(metricType==null) throw new IllegalArgumentException("Passed MetricType was null", new Throwable());		
		LongIntervalTraceValue<T> clone = new LongIntervalTraceValue<T>(this);
		super.cloneReset(metricType);
		if(!metricType.isSticky()) {
			max = 0;
			min = 0;
		}
		return clone;
	}	

	/**
	 * Creates a new LongIntervalTraceValue
	 */
	public LongIntervalTraceValue() {
		super(TraceValueType.INTERVAL_LONG_TYPE);
	}
	
	/**
	 * Creates a new LongIntervalTraceValue for the passed child type
	 * @param type The child TraceValueType
	 */
	public LongIntervalTraceValue(TraceValueType type) {
		super(type);
	}
	
	
	/**
	 * Aggregates the passed ITraceValue into this interval trace value
	 * @param value The ITraceValue to apply
	 */
	@Override
	public void apply(T value) {
		long v = value.getLongValue();
		if(this.count==0 || v>max) max = v;
		if(this.count==0 || v<min) min = v;
		super.apply(value);
	}
	

	
	/**
	 * Returns the primary value of this| grep trace value
	 * @return the primary value of this trace value
	 */
	public Object getValue() {
		return getAvg();
	}
	
	/**
	 * Returns the minimum value for the interval
	 * @return the minimum value for the interval
	 */
	public long getMin() {
		return min;
	}
	
	/**
	 * Returns the maximum value for the interval
	 * @return the maximum value for the interval
	 */
	public long getMax() {
		return max;
	}
	
	
	/**
	 * Returns the average value for the interval
	 * @return the average value for the interval
	 */
	public long getAvg() {
		//return (long)davg;
		return (long)davg;
	}

	
	/**
	 * Reads the state of this object in from the Object input stream
	 * @param in the stream to read data from in order to restore the object 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		min = in.readLong();
		max = in.readLong();
	}

	/**
	 * Writes this object out to the Object output stream
	 * @param out the stream to write the object to 
	 * @throws IOException
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeLong(min);
		out.writeLong(max);
	}

	/**
	 * Constructs a <code>String</code> with key attributes in name = value format.
	 * @return a <code>String</code> representation of this object.
	 */
	public String toString() {	    
	    StringBuilder retValue = new StringBuilder("\n\tLongIntervalTraceValue [")
	        .append(TAB).append("min = ").append(this.min)
	        .append(TAB).append("max = ").append(this.max)
	        .append(TAB).append("avg = ").append(getAvg())
	        .append(TAB).append("cnt = ").append(this.count)
	        .append("\n\t]");    
	    return retValue.toString();
	}
	

	/**
	 * Returns the Maximum as a {@link java.lang.Number}
	 * @return the Maximum
	 */
	@Override
	public Number getMaximum() {
		return getMax();
	}


	/**
	 * Returns the Minimum as a {@link java.lang.Number}
	 * @return the Minimum
	 */
	@Override
	public Number getMinimum() {
		return getMin();
	}

}

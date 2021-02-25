package havis.custom.harting.assignmentstore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import havis.device.rf.RFConsumer;
import havis.device.rf.RFDevice;
import havis.device.rf.exception.CommunicationException;
import havis.device.rf.exception.ConnectionException;
import havis.device.rf.exception.ImplementationException;
import havis.device.rf.exception.ParameterException;
import havis.device.rf.tag.Filter;
import havis.device.rf.tag.TagData;
import havis.device.rf.tag.operation.TagOperation;

public class RFConnector implements RFConsumer {
	private final static Logger LOG = Logger.getLogger(RFConnector.class.getName());
	private final static int TIME_OUT = 3000;

	private Lock lock = new ReentrantLock();
	private RFDevice device;
	private boolean connected;

	public RFConnector(RFDevice device) {
		try {
			lock.lock();

			if (this.device == device) {
				return;
			}

			if (connected) {
				try {
					this.device.closeConnection();
				} catch (ConnectionException e) {
					LOG.log(Level.WARNING, "Exception on closing device connection", e);
				}
			}
			connected = false;
			this.device = device;
		} finally {
			lock.unlock();
		}
	}

	public List<TagData> getTags() throws ParameterException, ImplementationException, ConnectionException, CommunicationException {
		return getOpenController().execute(Arrays.asList((short) 0), new ArrayList<Filter>(), new ArrayList<TagOperation>());
	}

	public List<TagData> getTags(String id, List<TagOperation> tagOperations) throws ParameterException, ImplementationException, ConnectionException,
			CommunicationException {

		List<Filter> filterList = createFilter(id);

		return getOpenController().execute(Arrays.asList((short) 0), filterList, tagOperations);
	}

	@Override
	public void connectionAttempted() {
		try {
			lock.lock();

			if (connected) {
				connected = false;
				device.closeConnection();
			}
		} catch (ConnectionException e) {
			LOG.log(Level.SEVERE, "Failed to close connection", e);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public List<TagOperation> getOperations(TagData arg0) {
		return null;
	}

	@Override
	public void keepAlive() {
	}

	/**
	 * Get the RFDevice
	 * 
	 * @return RFDevice
	 * @throws ImplementationException
	 * @throws ConnectionException
	 * @throws NamingException
	 */
	private RFDevice getOpenController() throws ConnectionException, ImplementationException {
		try {
			lock.lock();

			if (device == null) {
				throw new ConnectionException("No RF controller instance available.");
			}

			if (!connected) {
				device.openConnection(this, TIME_OUT);
				connected = true;
			}

			return device;
		} finally {
			lock.unlock();
		}
	}

	private List<Filter> createFilter(String id) throws ParameterException {
		List<Filter> filterList = new ArrayList<Filter>();

		if ((id != null) && ((id.trim().length() % 4)) == 0) {
			byte[] data = DatatypeConverter.parseHexBinary(id.trim());
			
			Filter filter = new Filter();
			filter.setData(data); 
			filter.setBank((short) 1); // EPC Bank
			filter.setBitOffset((short) 32); // offset = 2 words (CRC+PC)
			filter.setBitLength((short) (data.length * 8)); //convert to bit
			filter.setMask(createFilterMask(data.length));
			filter.setMatch(true);

			filterList.add(filter);
		} else  {
			throw new ParameterException("Invalid id length. Multiple of 16 (in bits) expected.");			
		}
		
		return filterList;
	}
	
	private byte[] createFilterMask(int len) {
		byte[] mask = new byte[len];
		for (int i = 0; i < mask.length; i++)
			mask[i] = (byte) 0xff;
		return mask;
	}
}
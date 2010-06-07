package org.hydra.db.server;

import java.util.EnumMap;

import org.hydra.db.beans.ColumnBean;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.db.beans.KeyspaceBean;
import org.hydra.db.beans.ColumnBean.COLUMN_TYPES;
import org.hydra.utils.abstracts.ALogger;

public class CassandraVirtualPath extends ALogger {
	
	// Error codes
	public enum ERR_CODES {
		UNDEFINED, 
		NO_ERROR, 
		INVALID_DESCRIPTOR, 
		INVALID_PATH, 
		INVALID_KS, 
		INVALID_CF, 
		INVALID_COLUMN, 
		INVALID_LINK, 
		INVALID_PATH_STRUCTURE, 
		INVALID_DIC_TYPE, 
		INVALID_LINKID,
	};

	ERR_CODES _errCode = ERR_CODES.UNDEFINED;
	
	public ERR_CODES getErrorCode() {
		return _errCode;
	}
	
	String _errString = null;
	
	public String getError(){
		return _errString;
	}
	
	private void setError(String inErrString){
		getLog().error(inErrString);
		_errString = inErrString;
	}

	// Path parts
	public enum PARTS {
		P1_KSP, P2_CF, P3_KEY, P4_SUPER, P5_COL
	};

	// Result types
	public enum PATH_TYPE {
		UNDEFINED,							/* undefined */
		KSP___CF,							/* all super columns */
		KSP___CF___COLUMNS, 				/* description of super columns */
		KSP___CF___LINKS, 					/* description of links */
		KSP___CF___ID,				 		/* super column */
		KSP___CF___ID___COL,				/* column of super column */
		KSP___CF___ID___LINKNAME,			/* all links */
		KSP___CF___ID___LINKNAME__LINKID,	/* link */ 
	};

	// fields according to path type
	// ... beans
	private CassandraDescriptorBean _descriptor;
	public KeyspaceBean _kspBean = null;
	public ColumnFamilyBean _cfBean = null;
	public ColumnFamilyBean _linkCf = null;	
	public ColumnBean _colBean = null;
	// ... path && path type
	PATH_TYPE _pathType = PATH_TYPE.UNDEFINED;
	// ... others
	private String _path = null;
	private String _ID = null;
	private String _linkName = null;
	private String _linkID = null;
	public String getPath(){
		return _path;
	}
	public String getID() {
		return _ID;
	}
	public String getLinkName() {
		return _linkName;
	}
	public String getLinkID() {
		return _linkID;
	}

	// Map contains parsed parts of access path
	public EnumMap<PARTS, String> _pathMap = new EnumMap<PARTS, String>(
			PARTS.class);

	// Path definitions as array
	String[] _pathDefs = null;

	// Access path string delimeter
	final String PATH_DELIMETER = "\\.";


	public CassandraVirtualPath(
			CassandraDescriptorBean inCassandraDescriptorBean, String inPath) {
		if(validateAndInitParameters(inCassandraDescriptorBean, inPath)){			
			_path = inPath;
			_descriptor = inCassandraDescriptorBean;
			
			getLog().debug("Parsing access path: " + inPath);
			parseVirtualString();
		}
	}

	private boolean validateAndInitParameters(CassandraDescriptorBean inCassandraDescriptorBean, String inPath){
		if (inCassandraDescriptorBean == null) {
			setError("CassandraDescriptor is NULL");
			_errCode = ERR_CODES.INVALID_DESCRIPTOR;
			return false;
		} else if (inPath == null) {
			setError("Access path is NULL");
			_errCode = ERR_CODES.INVALID_PATH;
			return false;
		}
		// * split access path to definitions
		_pathDefs = inPath.split(PATH_DELIMETER);
		// * validate access path parts length...
		if (_pathDefs.length < 2 || _pathDefs.length > PARTS.values().length) {
			setError("Cassandra access path definition length(tokens) is valid: " + inPath);
			_errCode = ERR_CODES.INVALID_PATH_STRUCTURE;
			return false;
		}
		
		// * ... if OK, split access path to mapPartString
		getLog().debug("Parsing access info for: " + inPath);
		for (int i = 0; i < _pathDefs.length; i++) {
			_pathMap.put(PARTS.values()[i], _pathDefs[i]);
			getLog().debug(
					String.format("%s. PathParts.%-8s = %s", i,
							PARTS.values()[i], _pathDefs[i]));
		}		
		return true;
	}
	
	private void parseVirtualString() {		
		switch (_pathMap.size()) {
		case 2:
			if(init2Parameters()){
				getLog().debug("Parsed 2 parameters for:" + _path);
			}else{
				getLog().error("Error due parsing 2 parameters for:" + _path);
			}
			break;
		case 3:
			if(init2Parameters() 
					&&	init3Parameters()){
				getLog().debug("Parsed 3 parameters for:" + _path);
			}else{
				getLog().error("Error due parsing 3 parameters for:" + _path);
			}
			break;
		case 4:
			if(init2Parameters()
					&& init3Parameters()
					&& init4Parameters()){
				getLog().debug("Parsed 4 parameters for:" + _path);
			}else{
				getLog().error("Error due parsing 4 parameters for:" + _path);
			}
			break;
		case 5:
			if(init2Parameters()
					&& init3Parameters()
					&& init4Parameters()
					&& init5Parameters()){
				getLog().debug("Parsed 5 parameters for:" + _path);
			}else{
				getLog().error("Error due parsing 5 parameters for:" + _path);
			}
			break;			
		default:
			_errCode = ERR_CODES.INVALID_PATH_STRUCTURE;
			_errString = "Invalid access path length: " + _pathMap.size();
		}
	}

	private boolean init5Parameters() {
		if(_pathMap.get(PARTS.P5_COL) == null){
			_errCode = ERR_CODES.INVALID_LINKID;
			_errString = "Invalid link ID!";
			return false;
		}
		// finish
		_linkID  = _pathMap.get(PARTS.P5_COL);
		_errCode = ERR_CODES.NO_ERROR;
		_pathType = PATH_TYPE.KSP___CF___ID___LINKNAME__LINKID;
		return true;
	}

	private boolean init4Parameters() {
		// * [Optional] validate 4th parts
		if (_cfBean.getColumns().containsKey(_pathMap.get(PARTS.P4_SUPER))) {
			_colBean = _cfBean.getColumns().get(_pathMap.get(PARTS.P4_SUPER));
			_pathType = PATH_TYPE.KSP___CF___ID___COL;
			_errCode = ERR_CODES.NO_ERROR;
			return true;
		}else if(_cfBean.getLinks().containsKey(_pathMap.get(PARTS.P4_SUPER))){
			_linkName = _pathMap.get(PARTS.P4_SUPER);
			_colBean = _cfBean.getLinks().get(_pathMap.get(PARTS.P4_SUPER));
			_linkCf = _kspBean.getColumnFamilyByName(_pathMap.get(PARTS.P4_SUPER));
			_pathType = PATH_TYPE.KSP___CF___ID___LINKNAME;
			_errCode = ERR_CODES.NO_ERROR;
			return true;
		}		
		setError(String.format("Invalid column (%s)!",
				_pathMap.get(PARTS.P4_SUPER)));
		_pathType = PATH_TYPE.UNDEFINED;
		_errCode = ERR_CODES.INVALID_COLUMN;
		return false;
	}

	private boolean init3Parameters() {
		try {
			COLUMN_TYPES columnType = COLUMN_TYPES.valueOf(_pathMap
					.get(PARTS.P3_KEY));
			if (columnType == COLUMN_TYPES.COLUMNS) {
				_pathType = PATH_TYPE.KSP___CF___COLUMNS;
				_errCode = ERR_CODES.NO_ERROR;
			} else if (columnType == COLUMN_TYPES.LINKS) {
				_pathType = PATH_TYPE.KSP___CF___LINKS;
				_errCode = ERR_CODES.NO_ERROR;
			} else {
				setError("Invalid column type: " + columnType.toString());
				_errCode = ERR_CODES.INVALID_DIC_TYPE;
			}				
		} catch (Exception e) {
			getLog().error("Could not find predefined column type: " + _pathMap.get(PARTS.P3_KEY));
			_pathType = PATH_TYPE.KSP___CF___ID;
			_ID = _pathMap.get(PARTS.P3_KEY);
			_errCode = ERR_CODES.NO_ERROR;
		}
		return _errCode == ERR_CODES.NO_ERROR;
	}

	private boolean init2Parameters() {
		// * [Mandatory] validate keyspace...
		getLog().debug("Validate for keyspace: " + getPathPart(PARTS.P1_KSP));
		if (!_descriptor.containsKeyspace(getPathPart(PARTS.P1_KSP))) {
			setError("Could not find keyspace: " + getPathPart(PARTS.P1_KSP));
			_errCode = ERR_CODES.INVALID_KS;
			return false;
		}
		_kspBean = _descriptor.getKeyspace(getPathPart(PARTS.P1_KSP));

		// * [Mandatory] validate column family...
		getLog().debug("Validate for column family: " + getPathPart(PARTS.P2_CF));
		if (!_kspBean.containsColumnFamily(getPathPart(PARTS.P2_CF))) {
			setError("Could not find column family: " + getPathPart(PARTS.P2_CF));
			_errCode = ERR_CODES.INVALID_CF;
			return false;
		}
		_cfBean = _kspBean.getColumnFamilyByName(_pathMap.get(PARTS.P2_CF));
		_errCode = ERR_CODES.NO_ERROR;
		_pathType = PATH_TYPE.KSP___CF;
		return true;
	}

	public String getPathPart(PARTS inPathPart) {
		return _pathMap.get(inPathPart);
	}

	public PATH_TYPE getPathType() {
		return _pathType;
	}

	public EnumMap<PARTS, String> getPathMaps() {
		return _pathMap;
	}
	
	public CassandraDescriptorBean getDescriptor() {
		return _descriptor;
	}

	
}

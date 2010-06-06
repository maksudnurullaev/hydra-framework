package org.hydra.db.server;

import java.util.EnumMap;

import org.hydra.db.beans.ColumnBean;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.db.beans.KeyspaceBean;
import org.hydra.db.beans.ColumnBean.COLUMN_TYPES;
import org.hydra.utils.abstracts.ALogger;

public class CassandraVirtualPath extends ALogger {
	// Access path beans - needs for "same time initialization"
	public KeyspaceBean kspBean = null;
	public ColumnFamilyBean cfBean = null;
	public ColumnBean colBean = null;
	
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
		INVALID_DIC_TYPE
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

	PATH_TYPE _resultType = PATH_TYPE.UNDEFINED;

	// Map contains parsed parts of access path
	public EnumMap<PARTS, String> _pathMap = new EnumMap<PARTS, String>(
			PARTS.class);

	// Path definitions as array
	String[] _pathDefs = null;

	// Access path string delimeter
	final String PATH_DELIMETER = "\\.";

	// To save original access path
	private String _path = null;
	private CassandraDescriptorBean _descriptor;

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
			init2Parameters();
			break;
		case 3:
			init2Parameters();
			init3Parameters();
			break;
		case 4:
			init2Parameters();
			init3Parameters();
			init4Parameters();
			break;
		default:
			_errCode = ERR_CODES.INVALID_PATH_STRUCTURE;
			_errString = "Invalid access path length: " + _pathMap.size();
		}
	}

	private void init4Parameters() {
		// * [Optional] validate 4th parts
		if (cfBean.getColumns().containsKey(_pathMap.get(PARTS.P4_SUPER))) {
			colBean = cfBean.getColumns().get(_pathMap.get(PARTS.P4_SUPER));
			_resultType = PATH_TYPE.KSP___CF___ID___COL;
		}else if(cfBean.getLinks().containsKey(_pathMap.get(PARTS.P4_SUPER))){
			colBean = cfBean.getLinks().get(_pathMap.get(PARTS.P4_SUPER));
			_resultType = PATH_TYPE.KSP___CF___ID___LINKNAME;
			
		}else{
			setError(String.format("Invalid column (%s)!",
					_pathMap.get(PARTS.P4_SUPER)));
			_resultType = PATH_TYPE.UNDEFINED;
			_errCode = ERR_CODES.INVALID_COLUMN;
			return;
			
		}
		_errCode = ERR_CODES.NO_ERROR;
	}

	private void init3Parameters() {
		try {
			COLUMN_TYPES columnType = COLUMN_TYPES.valueOf(_pathMap
					.get(PARTS.P3_KEY));
			if (columnType == COLUMN_TYPES.COLUMNS) {
				_resultType = PATH_TYPE.KSP___CF___COLUMNS;
				_errCode = ERR_CODES.NO_ERROR;
			} else if (columnType == COLUMN_TYPES.LINKS) {
				_resultType = PATH_TYPE.KSP___CF___LINKS;
				_errCode = ERR_CODES.NO_ERROR;
			} else {
				setError("Invalid column type: " + columnType.toString());
				_errCode = ERR_CODES.INVALID_DIC_TYPE;
			}				
		} catch (Exception e) {
			getLog().error("Could not find predefined column type: " + _pathMap.get(PARTS.P3_KEY));
			_resultType = PATH_TYPE.KSP___CF___ID;
			_errCode = ERR_CODES.NO_ERROR;
		}
	}

	private void init2Parameters() {
		// * [Mandatory] validate keyspace...
		getLog().debug("Validate for keyspace: " + getPathPart(PARTS.P1_KSP));
		if (!_descriptor.containsKeyspace(getPathPart(PARTS.P1_KSP))) {
			setError("Could not find keyspace: " + getPathPart(PARTS.P1_KSP));
			_errCode = ERR_CODES.INVALID_KS;
			return;
		}
		kspBean = _descriptor.getKeyspace(getPathPart(PARTS.P1_KSP));

		// * [Mandatory] validate column family...
		getLog().debug("Validate for column family: " + getPathPart(PARTS.P2_CF));
		if (!kspBean.containsColumnFamily(getPathPart(PARTS.P2_CF))) {
			setError("Could not find column family: " + getPathPart(PARTS.P2_CF));
			_errCode = ERR_CODES.INVALID_CF;
			return;
		}
		cfBean = kspBean.getColumnFamilyByName(_pathMap.get(PARTS.P2_CF));
		_errCode = ERR_CODES.NO_ERROR;
		_resultType = PATH_TYPE.KSP___CF;		
	}

	public String getPathPart(PARTS inPathPart) {
		return _pathMap.get(inPathPart);
	}

	public PATH_TYPE getPathType() {
		return _resultType;
	}

	public EnumMap<PARTS, String> getPathMaps() {
		return _pathMap;
	}

	public String getPath() {
		return _path;
	}

	public CassandraDescriptorBean getDescriptor() {
		return _descriptor;
	}
}

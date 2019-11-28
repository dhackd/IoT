// register
const register = () => {
  let deviceInfo = {
    'deviceId' : $('#regDevice_id').val(),
    'typeCode' : $('#regDevice_type').val(),
    'deviceAddr' : $('#regDevice_addr').val(),
  }
  
  if(!_checkValues(deviceInfo)) {
	  return;
  }

  _registerDevice(deviceInfo)
}

const _checkValues = (deviceInfo) => {
  let result = true;
  const keys = Object.keys(deviceInfo);

  const nameMap = {
  	  'deviceId' : "기기명",
  	  'typeCode' : "기기타입",
  	  'deviceAddr' : '기기주소'
  }
  
  // null check
  for (i =0; i < keys.length; i++) {
	  // null check
	  if(deviceInfo[keys[i]] === '') {
		  result = false;
		  alert('"' + nameMap[keys[i]] + '" 값이 없습니다.')
		  break;
	  }
  }
  
  // address check
  const addr = deviceInfo['deviceAddr'];
  if (!(addr.startsWith("Am") && addr.length === 52)) {
	  alert('주소가 잘못되었습니다.');
	  result = false;
  }
  
  return result;
}

const _registerDevice = (deviceInfo) => {
  $.ajax({
      type : "POST",
    url : apiV1 + "/devices",
    data: deviceInfo,
    success : function(res) {
	      alert("등록이 완료되었습니다.")
	      _clean()
	      $('#deviceModal').modal('toggle');
	      search();
    },
    error : function(err) {
    	alert(err.responseJSON.message)
    }
  })
}

const _clean = () => {
  $('#regDevice_id').val(''),
  $('#regDevice_type').val(''),
  $('#regDevice_addr').val('')
}
function getDeviceList(page) {	
	const search = {
		type : $('#type_list').val() === null ? "": $('#type_list').val(),
		startDate : $('#startDate input').val() === '' ? "": $('#startDate input').val(),
		endDate : $('#endDate input').val() === '' ? "": $('#endDate input').val(),
		searchType: $('#search_type').val(),
		searchData: $('#search_data').val()
	}
	
	if(search.searchType === '' && search.searchData !== '') {
		alert('검색 타입을 선택해주세요.')
		return;
	}
	
	if(search.searchType !== '' && search.searchData === '') {
		alert('검색 내용을 입력해주세요.')
		return;
	}

	$.ajax({
		 type : "GET",
		 url : apiV1 + "/devices/" + page + '?' +_addQuery('type', search.type) + _addQuery('startDate', search.startDate) +
		 _addQuery('endDate', search.endDate) + _addQuery('searchType', search.searchType) +_addQuery('searchData', search.searchData),
		 success : function(res) {
			 _saveSearchStatus(search)
			 setPageInfo(res.data)
			 _renderDevices(res.data.content)
		 }
	 })
  }
  
  function _renderDevices(deviceList) {
    let devices = $('#deviceManageTable__body');
    let list='';

    deviceList.forEach(device => {
      list +='<div class="table-row"><div class="c1 table-cell"><b>'+ device.deviceId + '</b></div><div class="c2 table-cell"><b>'+ device.deviceType.name + 
      '</b></div><div class="c3 table-cell">'+ device.deviceAddr + '</div><div class="c4 table-cell">'+ moment(new Date(device.createAt)).format('YYYY-MM-DD HH:mm:ss') +
      '</div><div class="c5 table-cell" id="' + device.deviceId + '_form"><button class="delete" onClick=_removeConfirm("'+ device.deviceId + '")>Delete</button>' +
      '</div></div>'
    })
    
    devices.html(list)
  }
  
  function _removeConfirm(deviceId) {
	  const obj = $('#' + deviceId + '_form')
	  
	  const errorConfirm = '<div class="errorConfirm"><span>Remove your device?</span> ' +
		  '<button onClick=_removeDevice("'+ deviceId + '")>Yes</button>' +
		  '<button onClick=_cancelRemove("'+ deviceId +'")>Cancel</button>' +
		  '</div>';
	  
	  obj.html(errorConfirm)
  }
  
  function _cancelRemove(deviceId) {
	  const obj = $('#' + deviceId + '_form')
	  const back = '<button class="delete" onClick=_removeConfirm("'+ deviceId + '")>Delete</button>';
	  
	  obj.html(back);
  }
  
  function _removeDevice(deviceId) {
	  $.ajax({
		 type : "DELETE",
		 url : apiV1 + "/devices/" + deviceId ,
		 success : function(res) {
			 alert("삭제가 완료되었습니다.")
			 const page = window.sessionStorage.getItem("page");
			 search(page);
		 }
	 })
  }
  
  
  function _addQuery(key, val) {
	  return '&' + key + '=' + val;
  }
  
// search data manage
  function _saveSearchStatus(search) {
  	window.sessionStorage.setItem('search', JSON.stringify(search))
  }
  
  function _loadSearchStatus() {
	const searchStatus = window.sessionStorage.getItem('search');
	if(searchStatus === null) {
		return;
	}
	
	const searchJson = JSON.parse(searchStatus)
	$('#type_list').val(searchJson.type)
	$('#search_type').val(searchJson.searchType)
	$('#search_data').val(searchJson.searchData)
	$('#start_date').val(searchJson.startDate)
	$('#end_date').val(searchJson.endDate)
}

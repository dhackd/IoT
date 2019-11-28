function getGasRecepitList(page) {	
	const search = {
		startDate : $('#startDate input').val() === '' ? "": new Date($('#startDate input').val()).getTime() / 1000,
		endDate : $('#endDate input').val() === '' ? "": new Date($('#endDate input').val()).getTime() / 1000 + 86400,
		searchData: $('#search_data').val() === '' ? "": $('#search_data').val(),
		size: 10,
	}
	
	$.ajax({
		 type : "GET",
		 url : apiV1 + '/contracts/' + contractAddress + '/data?funcName=findDeviceHistory&args=' + 
		 search.searchData+ ',' +search.startDate + ',' + search.endDate + ',' +search.size+','+ page,
		 success : function(res) {
			 const responseData = JSON.parse(res.data)
			 setPageInfo(responseData)
			 _renderDevices(responseData.contents)
		 }
	 })
  }
  
  function _renderDevices(deviceList) {
    let devices = $('#gasManageTable__body');
    let list='';

    for (var i in deviceList) {
    	list +='<div class="table-row"><div class="c1 table-cell"><b>'+ deviceList[i].deviceId + '</b></div><div class="c2 table-cell"><b>'+ 
    	deviceList[i].flow + '</b></div><div class="c3 table-cell">'+ moment(new Date(deviceList[i].datetime * 1000)).format('YYYY-MM-DD HH:mm:ss') +
    	'</div><div class="c4 table-cell">free</div></div>'
    }
    
    devices.html(list)
  }
  
  function _addQuery(key, val) {
	  return '&' + key + '=' + val;
  }
const search = () => {
	let page = window.sessionStorage.getItem("page")
	page === null ? page = 0 : page = page;
	_getCheckerList(page)
}

function _getCheckerList(page) {
	$.ajax({
		type : "GET",
		url : apiV1 + "/devices/connections/" + page,
		success : function(res) {
			setPageInfo(res.data)
			_renderList(res.data.content)
		}
	})
}

function nameSort(a, b) {
	const aNum = a.deviceId.replace(/[^0-9]/g,"");
	const bNum = b.deviceId.replace(/[^0-9]/g,"");
	return aNum - bNum;
}

function _renderList(list) {
	const deviceDomId = $('#device_list');
	let deviceList = '';
	list.sort(nameSort)
	
	for (count in list) {
		const deviceId = list[count].deviceId;
		const address = list[count].deviceAddr;
		const type = list[count].deviceType.name;
		const isconnected = list[count].connection

		deviceList += '<div onClick=_goDetailPage("' + address + '") id="'
		+ address + '" class="device"><div class="row"><div class="title">' + deviceId + '</div>'
		
		if(isconnected) {
			deviceList += '<div class="connect_on"></div></div>' 
		} else {
			deviceList += '<div class="connect_off"></div></div>' 
		}
		deviceList +='<div class="data">'
			
		if(list[count].createAt < list[count].dataUpdatedAt) {
			const regDate = moment(new Date(list[count].dataUpdatedAt)).format('YYYY-MM-DD HH:mm:ss')
			deviceList += '<div class="date">Data Tx occured at '+ regDate + '</div>'
		} else {
			deviceList += '<div class="date">Data Tx not occured</div>'
		}
		
		deviceList += '</div>' + '<div class="data"><div class="key">Type</div><div class="value">'
		+ type + '</div></div>' + '<div class="data"><div class="key">Address</div><div class="value">'
		+ address + '</div></div>'+ '</div>'
	}
	$(deviceDomId).html(deviceList)
}

function _goDetailPage(address) {
  window.location.href = '/monitoring/devices/' + address
}

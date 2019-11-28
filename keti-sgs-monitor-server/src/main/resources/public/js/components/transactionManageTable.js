function getTransactionList(page) {
	const search = {
		type : $('#type_list').val() === null ? "": $('#type_list').val(),
		startDate : $('#startDate input').val() === '' ? "0": $('#startDate input').val(),
		endDate : $('#endDate input').val() === '' ? "0": $('#endDate input').val(),
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
		 url : apiV1 + "/transactions/pages/" + page + '?' +_addQuery('type', search.type) + _addQuery('startDate', search.startDate) +
		 _addQuery('endDate', search.endDate) + _addQuery('searchType', search.searchType) +_addQuery('searchData', search.searchData),
		 success : function(res) {
			 _saveSearchStatus(search)
			 setPageInfo(res.data)
			 _renderTransactions(res.data.content)
		 }
	 })
  }
  
function _renderTransactions(transactionList) {
    let transactionTable = $('#transactionManageTable__body');
    let list='';
    
    transactionList.forEach(device => {
      list +='<div class="table-row" onClick=_goDetailPage("' + device.txid +'")>'
      
      if(device.deviceId === null) {
    	  // deleted
    	  list+='<div class="c1 delete table-cell"></div>'
      } else {
    	  // normal
    	  list+='<div class="c1 normal table-cell">'+ device.deviceId +'</div>'
      }
      
      // divide transaction
//      if (device.from !== device.to) {
//    	  //smart contract
//    	  device.from = device.to !== "" ? device.from : 'Deploy Contract';
//    	  
//    	  list += '<div class="c2 table-cell">Manage</div>'+
//    	  '<div class="c3 table-cell">'+ device.from +'</div>'
//      } else {
//      }
    
	  // normal
	  const deviceType = device.type !== null ? device.type.name : '';
	  list +='<div class="c2 table-cell">'+ deviceType + '</div>'+
	  '<div class="c3 table-cell">'+ device.to +'</div>'
      
      const regDate = moment(new Date(device.regAt)).format('YYYY-MM-DD HH:mm:ss');
      list += '<div class="c4 table-cell">'+ device.txid +'</div><div class="c5 table-cell">'+ regDate +'</div></div>'
    })
    
    transactionTable.html(list)
}
  
function _goDetailPage (txid) {
	window.location.href='/transactionManage/' + txid;
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
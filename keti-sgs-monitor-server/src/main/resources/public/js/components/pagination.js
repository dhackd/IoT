function _fetchPage(now, max) {
	let pagination = $('#pagination')

	//component
	let activeFirst = '<li class="click" onClick=_first()><span >First</span></li>'
	let inActiveFirst = '<li class="click disabled" onClick=_first()><span >First</span></li>'

	let activeLast = '<li class="click" onClick=_final()><span>Last</span></li>';
	let inActiveLast = '<li class="click disabled" onClick=_final()><span>Last</span></li>';

	// prev Icon
	let activePrev = '<li class="click" onClick=_prev()><span class="fas fa-chevron-left"></span></li>'
	let inActivePrev = '<li class="click disabled" onClick=_prev()><span class="fas fa-chevron-left"></span></li>'

	// next Icon
	let activeNext = '<li class="click" onClick=_next()><span class="fas fa-chevron-right"></span></li>'
	let inActiveNext = '<li class="click disabled" onClick=_next()><span class="fas fa-chevron-right"></span></li>'

	let output = ''
	if (now != 1) {
		output += activeFirst
		output += activePrev
	} else {
		output += inActiveFirst
		output += inActivePrev
	}

	output += '<li class="page_state"><span>' + now + ' / ' + max+ '</span></li>'

	if (now < window.sessionStorage.getItem("totalPages")) {
		output += activeNext
		output += activeLast
	} else {
		output += inActiveNext
		output += inActiveLast
	}

	pagination.html(output)
}

function _page(page) {
	window.sessionStorage.setItem("page", page - 1)
	search()
}

function setPageInfo(data) {
	let nowPage = data.number + 1;

	window.sessionStorage.setItem("totalPages", data.totalPages)

	_fetchPage(nowPage, data.totalPages)
}

function _first() {
	_page(1)
}

function _final() {
	_page(window.sessionStorage.getItem("totalPages"))
}

function _prev() {
	let nowPage = parseInt(window.sessionStorage.getItem("page"))

	if (nowPage > 0) {
		_page(nowPage)
	}
}

function _next() {
	let nowPage = parseInt(window.sessionStorage.getItem("page"))
	let totalPages = window.sessionStorage.getItem("totalPages")
	let nextPage = nowPage + 2;
	if (nextPage <= totalPages) {
		_page(nextPage)
	}
}
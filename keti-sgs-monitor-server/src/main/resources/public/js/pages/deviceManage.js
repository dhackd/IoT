const search = () => {
	let page = window.sessionStorage.getItem("page")
	page === null ? page = 0 : page = page;
	getDeviceList(page)
}
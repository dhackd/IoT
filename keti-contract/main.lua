import './sql.lua'

function constructor()
  FlowManageService.createTable();
end

function save(deviceId, datetime, flow)
  FlowManageService:save(deviceId, datetime, flow);
end

function findAll()
  return FlowManageService:findAll();
end

-- multi parameter query
function findDeviceHistory(deviceId, startDate, endDate, pageSize, currentPage)
  return FlowManageService:findDeviceHistory(deviceId, startDate, endDate, pageSize, currentPage);
end

abi.register(save, findAll, findDeviceHistory)

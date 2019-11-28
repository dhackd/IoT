local FlowManageService = {};
function FlowManageService:new(o)
  setmetatable(o, {__index = self})
  return o
end

function FlowManageService:createTable()
  local flowTable = [[create table if not exists flow(
    deviceId text,
    datetime text,
    flow text
    )]]
  db.exec(flowTable)
end

function FlowManageService:save(deviceId, datetime, flow)
  local pstmt = db.prepare("insert into flow values (?,?,?)");
  pstmt:exec(deviceId, datetime, flow)
end

function FlowManageService:findAll()
  local pstmt = db.prepare("select * from flow")
  local result = pstmt:query();
  return _result2List(result)
end

function FlowManageService:findDeviceHistory(deviceId, startDate, endDate, pageSize, currentPage)
  local conditionQuery = _getDynamicQuery(deviceId, startDate, endDate);

  local number = 0;
  if currentPage ~= "" then
    number = tonumber(currentPage);
  end

  local totalPages = 1;
  if _getTotalCount(conditionQuery) > 1 then
    totalPages = math.floor((_getTotalCount(conditionQuery) - 1) / pageSize) + 1;
  end

  local resultForm = {
    number = number,
    contents = _getData(conditionQuery, pageSize, currentPage),
    totalPages = totalPages;
  }

  return resultForm;
end

function _getDynamicQuery(deviceId, startDate, endDate)
  local conditionQuery = "";
  -- multi query
  if deviceId ~= "" then
    conditionQuery = conditionQuery .. " where deviceId = " .. deviceId;
  end

  if startDate ~= "" and endDate ~= "" then
    if deviceId == "" then
      conditionQuery = conditionQuery .. " where datetime >= " .. startDate  .. " and  datetime < " .. endDate;
      else
      conditionQuery = conditionQuery .. " and datetime >= " .. startDate  .. " and  datetime < " .. endDate;
    end
  end

  return conditionQuery;
end

function _getTotalCount(conditionQuery)
  local countQuery = "select count(*) from flow";
  local pstmt = db.prepare(countQuery .. conditionQuery);

  local result = pstmt:query();
  return _result2number(result)
end

function _result2number(result)
  result:next();
  local count = result:get();
  return count;
end

function _getData(conditionQuery, pageSize, currentPage)
  local dataQuery = "select * from flow";

  local pagingQuery = "";
  if pageSize ~= "" and currentPage ~= "" then
    local limit = pageSize;
    local offset = currentPage * pageSize
    pagingQuery = " ORDER BY datetime DESC LIMIT " .. limit .. " OFFSET " .. offset;
  end

  local pstmt = db.prepare(dataQuery .. conditionQuery .. pagingQuery);
  local result = pstmt:query();
  return _result2List(result);
end

function _result2List(result)
  local list = {};
  while result:next() do
    local col1, col2, col3 = result:get()
    local item = {
      deviceId = col1,
      datetime = col2,
      flow = col3
    }
    table.insert(list, item)
  end
  return list
end

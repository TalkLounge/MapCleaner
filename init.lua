-- mods/mapcleaner/init.lua
-- =================
-- See README.txt for licensing and other information.

local areas = {}

local areasfile = io.open(minetest.get_worldpath() .."/areas.txt", "r")
if areasfile then
	for line in areasfile:lines() do
		local poses = string.split(line, ":")
		local pos1 = string.split(poses[1], ",")
		local pos2 = string.split(poses[2], ",")
		table.insert(areas, {pos1 = {x = tonumber(pos1[1]), y = tonumber(pos1[2]), z = tonumber(pos1[3])}, pos2 = {x = tonumber(pos2[1]), y = tonumber(pos2[2]), z = tonumber(pos2[3])}})
	end
	areasfile:close()
end

areasfile = io.open(minetest.get_worldpath() .."/areas.txt", "w")
areasfile:write("")
areasfile:close()

local function area_intersects(area1, area2)
	--area: {pos1 = {x = 0, y = 0, z = 0}, pos2 = {x = 0, y = 0, z = 0}}
	if area2.pos1.x <= area1.pos1.x and
		 area2.pos1.y <= area1.pos1.y and
		 area2.pos1.z <= area1.pos1.z and
		 area2.pos2.x >= area1.pos2.x and
		 area2.pos2.y >= area1.pos2.y and
		 area2.pos2.z >= area1.pos2.z then
		return true
	end
end

local function table_equals(t1, t2, ignore_mt)
	--stolen from: https://stackoverflow.com/questions/20325332/how-to-check-if-two-tablesobjects-have-the-same-value-in-lua#answer-30757399
	local ty1 = type(t1)
	local ty2 = type(t2)
	if ty1 ~= ty2 then
		return false
	end
	if ty1 ~= "table" and ty2 ~= "table" then
		return t1 == t2
	end
	local mt = getmetatable(t1)
	if not ignore_mt and mt and mt.__eq then
		return t1 == t2
	end
	for k1, v1 in pairs(t1) do
		local v2 = t2[k1]
		if v2 == nil or not table_equals(v1, v2) then
			return false
		end
	end
	for k2, v2 in pairs(t2) do
		local v1 = t1[k2]
		if v1 == nil or not table_equals(v1, v2) then
			return false
		end
	end
	return true
end

local file

file = io.open(minetest.get_worldpath() .."/areas.dat", "r")
if file then
	local data = minetest.deserialize(file:read("*a"))
	file:close()
	
	for keynew, valuenew in ipairs(data) do
		if not valuenew.parent then
			local insert = true
      local area = {pos1 = valuenew.pos1, pos2 = valuenew.pos2}
      for keyold, valueold in ipairs(areas) do
        if area_intersects(area, valueold) then
          insert = false
          break
        elseif area_intersects(valueold, area) and not table_equals(valueold, area) then
          table.remove(areas, keyold)
        end
      end
      if insert then
        table.insert(areas, area)
      end
    end
  end
end

areasfile = io.open(minetest.get_worldpath() .."/areas.txt", "a+")
for key, value in ipairs(areas) do
	areasfile:write(string.format("%s,%s,%s:%s,%s,%s", value.pos1.x, value.pos1.y, value.pos1.z, value.pos2.x, value.pos2.y, value.pos2.z) .."\n")
end

minetest.request_shutdown("")


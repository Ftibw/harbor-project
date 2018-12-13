local key = KEYS[1]
local limit = tonumber(ARGV[1])
local expire = ARGV[2]
local count = tonumber(redis.call("get", key)) or 0
if count < limit then
    redis.call("set", key, count + 1)
    redis.call("expire", key, expire)
    return 1
else return 0
end
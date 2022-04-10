if redis.call("get",KEY[1])==ARGV[1] then
    return redis.call("del",KEY[1])
else
    return 0
end
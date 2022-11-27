local nk = require("nakama")

local function custom_rpc_func(context, payload)
    nk.logger_info(string.format("Payload: %q", payload))

    -- "payload" is bytes sent by the client we'll JSON decode it.
    local json = nk.json_decode(payload)
    local groupId = json["GroupId"]
    local metadata = {}
    metadata["MatchID"] = json["MatchID"]
    nk.group_update(groupId, nil, "", "", "", "", "", nil, metadata, 0)
    return nk.json_encode(metadata)
end

nk.register_rpc(custom_rpc_func, "UpdateGroupMetadata")
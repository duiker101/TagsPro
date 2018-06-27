package net.duiker101.tagspro.tagspro.api


// TODO color?
data class TagCollection(var name: String = "New collection", var id: String = "") {
    var tags = ArrayList<Tag>()
}

data class Tag(var name: String = "Tag", @Transient var active: Boolean = false, var media_count: Int=0)

data class APITagSearchResponse(val hashtags: ArrayList<APITag>)

data class APITag(val hashtag: Tag, val position: Int)

package net.duiker101.tagspro.tagspro.api


// TODO color?
data class TagCollection(var name: String = "New collection", var id: String = "") {
    var tags = ArrayList<Tag>()

    @Transient
    var order: Int = 0
}

data class Tag(val pName: String, @Transient var active: Boolean = false, var media_count: Int = 0) {
    var name: String = "Tag"
        set(value) {
            field = if (!value.startsWith("#") && !value.startsWith("@"))
                "#$value"
            else
                value
        }

    init {
        this.name = pName
    }

}

data class APITagSearchResponse(val hashtags: ArrayList<APITag>)

data class APITag(val hashtag: Tag, val position: Int)

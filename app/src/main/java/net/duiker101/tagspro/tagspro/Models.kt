package net.duiker101.tagspro.tagspro


// TODO color?
data class TagCollection(var name: String = "New collection", var id: String = "", var expanded:Boolean) {
    var tags = ArrayList<Tag>()
}

data class Tag(var name: String = "Tag", @Transient var active: Boolean = false)

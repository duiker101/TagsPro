package net.duiker101.tagspro.tagspro

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.Index


// TODO color?
open class TagCollection(var name: String = "New collection") : RealmObject() {
    @Index
    var id: Long = 0
    var tags: RealmList<Tag> = RealmList()
}

open class Tag(
        var name: String = "Tag",
        @Ignore var active: Boolean=false
) : RealmObject() {
    @Index
    var id: Long = 0
}

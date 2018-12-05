package edu.uw.oazeemi.trashcache

data class ItemDetected(var itemName:String, var confidence:Float, var user:String){
    override fun toString(): String {
        return "{item: {name: ${this.itemName}, confidenceLevel: ${this.confidence}}, user: ${user}}"
    }

}
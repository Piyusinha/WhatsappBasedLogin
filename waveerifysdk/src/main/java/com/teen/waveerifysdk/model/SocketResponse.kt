package com.teen.waveerifysdk.model


import com.google.gson.annotations.SerializedName

internal data class SocketResponse(
    @SerializedName("documentKey")
    var documentKey: DocumentKey?,
    @SerializedName("fullDocument")
    var fullDocument: FullDocument?,
    @SerializedName("_id")
    var id: Id?,
    @SerializedName("ns")
    var ns: Ns?,
    @SerializedName("operationType")
    var operationType: String?
) {

    data class DocumentKey(
        @SerializedName("_id")
        var id: String?
    )

    data class FullDocument(
        @SerializedName("entry")
        var entry: List<Entry?>?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("object")
        var objectX: String?,
        @SerializedName("__v")
        var v: Int?
    ) {
        data class Entry(
            @SerializedName("changes")
            var changes: List<Change?>?,
            @SerializedName("id")
            var id: String?,
            @SerializedName("_id")
            var _id: String?
        ) {
            data class Change(
                @SerializedName("field")
                var `field`: String?,
                @SerializedName("_id")
                var id: String?,
                @SerializedName("value")
                var value: Value?
            ) {
                data class Value(
                    @SerializedName("contacts")
                    var contacts: List<Contact?>?,
                    @SerializedName("messages")
                    var messages: List<Message?>?,
                    @SerializedName("messaging_product")
                    var messagingProduct: String?,
                    @SerializedName("metadata")
                    var metadata: Metadata?
                ) {
                    data class Contact(
                        @SerializedName("_id")
                        var id: String?,
                        @SerializedName("profile")
                        var profile: Profile?,
                        @SerializedName("wa_id")
                        var waId: String?
                    ) {
                        data class Profile(
                            @SerializedName("name")
                            var name: String?
                        )
                    }

                    data class Message(
                        @SerializedName("from")
                        var from: String?,
                        @SerializedName("id")
                        var id: String?,
                        @SerializedName("_id")
                        var _id: String?,
                        @SerializedName("text")
                        var text: Text?,
                        @SerializedName("timestamp")
                        var timestamp: String?,
                        @SerializedName("type")
                        var type: String?
                    ) {
                        data class Text(
                            @SerializedName("body")
                            var body: String?
                        )
                    }

                    data class Metadata(
                        @SerializedName("display_phone_number")
                        var displayPhoneNumber: String?,
                        @SerializedName("phone_number_id")
                        var phoneNumberId: String?
                    )
                }
            }
        }
    }

    data class Id(
        @SerializedName("_data")
        var `data`: String?
    )

    data class Ns(
        @SerializedName("coll")
        var coll: String?,
        @SerializedName("db")
        var db: String?
    )
}
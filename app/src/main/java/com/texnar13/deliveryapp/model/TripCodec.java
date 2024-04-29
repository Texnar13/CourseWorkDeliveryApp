package com.texnar13.deliveryapp.model;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

import io.realm.mongodb.mongo.MongoClient;

/**
 * Mongo Decoder for Items.
 */
public class TripCodec {//implements CollectibleCodec<Trip> {

//    private final CodecRegistry registry;
//    private final Codec<Document> documentCodec;
//    private final TripConverter converter;
//
//    /**
//     * Default constructor.
//     */
//    public TripCodec() {
//        this.registry = MongoClient.getDefaultCodecRegistry();
//        this.documentCodec = this.registry.get(Document.class);
//        this.converter = new TripConverter();
//    }
//
//    /**
//     * Codec constructor.
//     * @param codec The existing codec to use.
//     */
//    public TripCodec(Codec<Document> codec) {
//        this.documentCodec = codec;
//        this.registry = MongoClient.getDefaultCodecRegistry();
//        this.converter = new TripConverter();
//    }
//
//    /**
//     * Registry constructor.
//     * @param registry The CodecRegistry to use.
//     */
//    public TripCodec(CodecRegistry registry) {
//        this.registry = registry;
//        this.documentCodec = this.registry.get(Document.class);
//        this.converter = new TripConverter();
//    }
//
//    /**
//     * Encode the passed Trip into a Mongo/BSON document.
//     * @param writer The writer to use for encoding.
//     * @param trip The Trip to encode.
//     * @param encoderContext The EncoderContext to use for encoding.
//     */
//    @Override
//    public void encode(
//            BsonWriter writer,
//            Trip trip,
//            EncoderContext encoderContext
//    ) {
//        Document document = this.converter.convert(trip);
//
//        documentCodec.encode(writer, document, encoderContext);
//    }
//
//    /**
//     * Get the class that this Codec works with.
//     * @return Returns the class that this Codec works with.
//     */
//    @Override
//    public Class<Trip> getEncoderClass() {
//        return Trip.class;
//    }
//
//    /**
//     * Decodes a Mongo/BSON document into an Trip.
//     * @param reader The reader containing the Document.
//     * @param decoderContext The DecoderContext to use for decoding.
//     * @return Returns the decoded Trip.
//     */
//    @Override
//    public Trip decode(BsonReader reader, DecoderContext decoderContext) {
//        Document document = documentCodec.decode(reader, decoderContext);
//        Trip trip = this.converter.convert(document);
//
//        return trip;
//    }
//
//    /**
//     * Generates a new ObjectId for the passed Trip (if absent).
//     * @param trip The Trip to work with.
//     * @return Returns the passed Trip with a new id added if there
//     * was none.
//     */
//    @Override
//    public Trip generateIdIfAbsentFromDocument(Trip trip) {
//        if (!documentHasId(trip)) {
//            trip.setId(new ObjectId());
//        }
//
//        return trip;
//    }
//
//    /**
//     * Returns whether or not the passed Trip has an id.
//     * @param Trip The Trip that you want to check for
//     * the presence of an id.
//     * @return Returns whether or not the passed Trip has an id.
//     */
//    @Override
//    public boolean documentHasId(Trip trip) {
//        return (trip.getName() != null);
//    }
//
//    /**
//     * Gets the id of the passed Trip. If there is no id, it will
//     * throw an IllegalStateException (RuntimeException).
//     * @param Trip The Trip whose id you want to get.
//     * @return Returns the id of the passed Trip as a BsonValue.
//     */
//    @Override
//    public BsonValue getDocumentId(Trip trip)
//    {
//        if (!documentHasId(trip)) {
//            throw new IllegalStateException("The document does not contain an _id");
//        }
//
//        return new BsonString(trip.getName());
//    }

}
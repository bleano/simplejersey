package com.mygraphql;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.TypeResolutionEnvironment;
import graphql.schema.*;
import graphql.schema.idl.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLNonNull.nonNull;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLUnionType.newUnionType;


/**
 * Created by bobdo on 8/13/2017.
 */


@Path("api")
public class Gql {
    static GraphQLSchema unionSchema = null;
    {

        GraphQLObjectType CatType = newObject()
                .name("Cat")
                .field(newFieldDefinition()
                        .name("name")
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name("meows")
                        .type(GraphQLBoolean))
                .build();


        GraphQLObjectType DogType = newObject()
                .name("Dog")
                .field(newFieldDefinition()
                        .name("name")
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name("barks")
                        .type(GraphQLBoolean)
                )
                .build();

        GraphQLUnionType PetType = newUnionType()
                .name("Pet")
                .possibleType(CatType)
                .possibleType(DogType)
                .typeResolver(new TypeResolver() {
                    @Override
                    public GraphQLObjectType getType(TypeResolutionEnvironment env) {
                        if (env.getObject() instanceof Dog) {
                            return DogType;
                        }
                        if (env.getObject() instanceof Cat) {
                            return CatType;
                        }
                        return null;
                    }
                })
                .build();

        GraphQLObjectType queryType = newObject()
                .name("Query")
                .field(newFieldDefinition()
                        .name("pets")
                        .type(new GraphQLList(PetType))
                        .dataFetcher(getPetsDataFetcher()))
                .build();

         unionSchema = GraphQLSchema.newSchema()
                .query(queryType)
                .build();

        System.out.println(" SCHEMA \r\n" + new SchemaPrinter().print(unionSchema));


    }

    public static class Cat{
        private final String name;

        private final boolean meows;

        public Cat(String name, boolean meows) {
            this.name = name;
            this.meows = meows;
        }
        public String getName() {
            return name;
        }
        public boolean isMeows() {
            return meows;
        }
    }
    public static class Dog {
        private final String name;
        private final boolean barks;

        public Dog(String name, boolean barks) {
            this.name = name;
            this.barks = barks;
        }
        public boolean isBarks() {
            return barks;
        }
        public String getName() {
            return name;
        }
    }

    public static DataFetcher getPetsDataFetcher(){
        return new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment env) {
                List<Object> pets = new ArrayList<>();
                pets.add(new Dog("the dog", true));
                pets.add(new Cat("the cat", true));
                return pets;
            }
        };
    }

    private static final LinkRepository linkRepository;
    static {
        //Change to `new MongoClient("mongodb://<host>:<port>/hackernews")`
        //if you don't have Mongo running locally on port 27017
        MongoDatabase mongo = new MongoClient().getDatabase("hackernews");
        linkRepository = new LinkRepository(mongo.getCollection("links"));
    }


    @Path("graphql")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMessage(@QueryParam("query") String query,
                             @QueryParam("variables") String variables,
                             @QueryParam("operationName") String operationName) {
        return getData(query, variables, operationName);
    }

    @Path("graphiql")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String getMessage(final String postBody) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> actualObj = mapper.readValue(postBody, Map.class);
            String query = (String)actualObj.get("query");
            return getData(query, null, null);
        }catch(IOException ioe){
            return ioe.getMessage();
        }
    }


    private String getData(final String query, final String variables,
        final String operationName){
        GraphQL graphQL = GraphQL.newGraphQL(unionSchema).build();
        ExecutionResult executionResult = graphQL.execute(query, operationName, variables);
        final Map<String, Object> result = new HashMap<>();
        result.put("data", executionResult.getData());
        final ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.writeValueAsString(result);
        }catch(JsonProcessingException jpe){
            return executionResult.getData().toString();
        }
    }
}
module Main exposing (..)

import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (..)
import Http
import Json.Decode as Decode exposing (int, string, Decoder, field)
import Debug exposing (log, crash)


main : Program Flags Model Msg
main =
    Html.programWithFlags
        { init = init
        , view = view
        , update = update
        , subscriptions = subscriptions
        }


init : Flags -> ( Model, Cmd Msg )
init { host } =
    let
        urlPrefix =
            "http://" ++ host ++ "/api/"

        queryCountry =
            "zim"
    in
        ( Model urlPrefix queryCountry Nothing Nothing
        , makeQuery urlPrefix (AirportsQuery queryCountry)
        )



-- MODEL


type alias Flags =
    { host : String
    }


type alias Model =
    { urlPrefix : String
    , queryCountry : String
    , queryResult : Maybe QueryResult
    , error : Maybe String
    }


type QueryData
    = AirportsQuery String
    | TopCountriesQuery
    | RunwayTypesQuery


type QueryResult
    = AirportsResult Airports
    | TopCountriesResult CountriesWithCount
    | RunwayTypesResult CountriesWithRunways


type alias Airports =
    List Airport


type alias Airport =
    { name : String
    }


type alias CountriesWithCount =
    List CountryWithCount


type alias CountryWithCount =
    { code : String
    , name : String
    , airportsCount : Int
    }


type alias CountriesWithRunways =
    List CountryWithRunways


type alias CountryWithRunways =
    { code : String
    , name : String
    , runways : List String
    }



-- UPDATE


type Msg
    = UpdateCountry String
    | RunQuery QueryData
    | ProcessQueryResult (Result Http.Error QueryResult)


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    log
        ("msg: "
            ++ (toString msg)
        )
        (case msg of
            RunQuery queryData ->
                ( model, makeQuery model.urlPrefix queryData )

            ProcessQueryResult (Ok queryResult) ->
                ( { model | queryResult = Just queryResult, error = Nothing }, Cmd.none )

            ProcessQueryResult (Err error) ->
                ( { model | queryResult = Nothing, error = Just (toString error) }, Cmd.none )

            UpdateCountry country ->
                ( { model | queryCountry = country }, Cmd.none )
        )


makeQuery : String -> QueryData -> Cmd Msg
makeQuery host queryData =
    let
        query =
            case queryData of
                AirportsQuery country ->
                    queryAirports country

                TopCountriesQuery ->
                    queryTopCountries

                RunwayTypesQuery ->
                    queryRunways
    in
        query host


queryAirports : String -> String -> Cmd Msg
queryAirports country host =
    querySomething (host ++ "airports/" ++ country) decodeAirportsQueryResult


queryTopCountries : String -> Cmd Msg
queryTopCountries host =
    querySomething (host ++ "report/topcountries") decodeTopCountriesQueryResult


queryRunways : String -> Cmd Msg
queryRunways host =
    querySomething (host ++ "report/runways") decodeRunwaysQueryResult


querySomething : String -> Decoder QueryResult -> Cmd Msg
querySomething uri decoder =
    let
        request =
            Http.get uri decoder
    in
        Http.send ProcessQueryResult request


decodeAirportsQueryResult : Decoder QueryResult
decodeAirportsQueryResult =
    Decode.map AirportsResult (Decode.at [ "airports" ] airportsDecoder)


airportsDecoder : Decoder (List Airport)
airportsDecoder =
    Decode.list airportDecoder


airportDecoder : Decoder Airport
airportDecoder =
    Decode.map Airport
        (field "name" string)


decodeTopCountriesQueryResult : Decoder QueryResult
decodeTopCountriesQueryResult =
    Decode.map TopCountriesResult (Decode.at [ "countries" ] countriesWithCountDecoder)


countriesWithCountDecoder : Decoder (List CountryWithCount)
countriesWithCountDecoder =
    Decode.list countryWithCountDecoder


countryWithCountDecoder : Decoder CountryWithCount
countryWithCountDecoder =
    Decode.map3 CountryWithCount
        (field "code" string)
        (field "name" string)
        (field "airportsCount" int)


decodeRunwaysQueryResult : Decoder QueryResult
decodeRunwaysQueryResult =
    Decode.map RunwayTypesResult (Decode.at [ "countries" ] countriesWithRunwaysDecoder)


countriesWithRunwaysDecoder : Decoder (List CountryWithRunways)
countriesWithRunwaysDecoder =
    Decode.list countryWithRunwaysDecoder


countryWithRunwaysDecoder : Decoder CountryWithRunways
countryWithRunwaysDecoder =
    Decode.map3 CountryWithRunways
        (field "code" string)
        (field "name" string)
        (field "runways" (Decode.list string))



-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.none



-- VIEW


view : Model -> Html Msg
view model =
    div []
        [ viewQueriesPanel model.queryCountry
        , queryResultDisplay model.queryResult model.error
        ]


viewQueriesPanel : String -> Html Msg
viewQueriesPanel country =
    div []
        [ span []
            [ input [ type_ "text", defaultValue country, onInput UpdateCountry ] []
            , button [ onClick <| RunQuery <| AirportsQuery country ] [ text "Airports" ]
            ]
        , span []
            [ button [ onClick <| RunQuery <| TopCountriesQuery ] [ text "Top Countries" ]
            ]
        , span []
            [ button [ onClick <| RunQuery <| RunwayTypesQuery ] [ text "Runways" ]
            ]
        ]


queryResultDisplay : Maybe QueryResult -> Maybe String -> Html msg
queryResultDisplay maybeQueryResult maybeError =
    case ( maybeQueryResult, maybeError ) of
        ( Nothing, Nothing ) ->
            text "Loading"

        ( Just queryResult, _ ) ->
            resultDisplay queryResult

        ( _, Just error ) ->
            text ("Error: " ++ error)


resultDisplay : QueryResult -> Html msg
resultDisplay result =
    case result of
        AirportsResult results ->
            airportsDisplay results

        TopCountriesResult result ->
            countriesWithCountDisplay result

        RunwayTypesResult result ->
            countriesWithRunwaysDisplay result


airportsDisplay : List Airport -> Html msg
airportsDisplay airports =
    table [ tableStyle ]
        (List.concat
            [ [ thead []
                    [ th [ tableStyle ] [ text "Name" ]
                    ]
              ]
            ]
            ++ (List.map airportDisplay airports)
        )


airportDisplay : Airport -> Html msg
airportDisplay airport =
    tr []
        [ td [ tableStyle ] [ text airport.name ]
        ]


countriesWithCountDisplay : List CountryWithCount -> Html msg
countriesWithCountDisplay countries =
    table [ tableStyle ]
        (List.concat
            [ [ thead []
                    [ th [ tableStyle ] [ text "Code" ]
                    , th [ tableStyle ] [ text "Name" ]
                    , th [ tableStyle ] [ text "Airports" ]
                    ]
              ]
            ]
            ++ (List.map countryWithCountDisplay countries)
        )


countryWithCountDisplay : CountryWithCount -> Html msg
countryWithCountDisplay country =
    tr []
        [ td [ tableStyle ] [ text country.code ]
        , td [ tableStyle ] [ text country.name ]
        , td [ tableStyle ] [ text (toString country.airportsCount) ]
        ]


countriesWithRunwaysDisplay : List CountryWithRunways -> Html msg
countriesWithRunwaysDisplay countries =
    table [ tableStyle ]
        (List.concat
            [ [ thead []
                    [ th [ tableStyle ] [ text "Code" ]
                    , th [ tableStyle ] [ text "Name" ]
                    , th [ tableStyle ] [ text "Runways" ]
                    ]
              ]
            ]
            ++ (List.map countryWithRunwaysDisplay countries)
        )


countryWithRunwaysDisplay : CountryWithRunways -> Html msg
countryWithRunwaysDisplay country =
    tr []
        [ td [ tableStyle ] [ text country.code ]
        , td [ tableStyle ] [ text country.name ]
        , td [ tableStyle ] [ text (String.join ", " country.runways) ]
        ]


tableStyle : Attribute msg
tableStyle =
    style [ ( "border", "1px solid black" ) ]

module Reactor exposing (..)

import Main exposing (..)
import Html


main : Program Never Model Msg
main =
    Html.program
        { init = init { host = "localhost:8080" }
        , view = view
        , update = update
        , subscriptions = subscriptions
        }

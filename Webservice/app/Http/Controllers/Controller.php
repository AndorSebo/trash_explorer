<?php

namespace App\Http\Controllers;

use Laravel\Lumen\Routing\Controller as BaseController;

class Controller extends BaseController
{
  public function Datareturn($bool, $number, $data, $message){
      return response()->json([
         'success' =>  $bool,
          'message' => $message,
          'data' => $data,
      ],$number);
  }
}

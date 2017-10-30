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

  public function LoginDatareturn($bool, $number, $data1, $data2, $data3, $message){
        return response()->json([
            'success' => $bool,
            'message' => $message,
            'userid' => $data2,
            'user' => $data1,
            'permission' => $data3
        ],$number);
  }  

}

<?php namespace App\Http\Controllers;

use Dingo\Api\Http\Request;
use Tymon\JWTAuth\JWTAuth;
use Tymon\JWTAuth\Exceptions\JWTException;
use App\Models\ApiSubscriber;
use Illuminate\Support\Facades\Hash;

class AuthenticateController extends Controller
{
    protected $auth;

    public function __construct(JWTAuth $auth)
    {
        $this->auth = $auth;
    }

    public function backend(Request $request)
    {
        // grab credentials from the request
        $credentials = $request->only('email', 'password');

        try {
            // attempt to verify the credentials and create a token for the user
            if (! $token = $this->auth->attempt($credentials)) {
                return $this->Datareturn(false, 401, [], "invalid_credentials");
            }
        } catch (JWTException $e) {
            // something went wrong whilst attempting to encode the token
            return response()->json(['error' => 'could_not_create_token'], 500);
        }

        // all good so return the token
        $userid = $this->auth->user()->user_id;
        return $this->LoginDatareturn(true, 200, compact('token'), $userid, "success_signin");
    }

    public function signUp(Request $request){
      $name = $request->name;
      $email = $request->email;
      $password = $request->password;
      $repassword = $request->repassword;

      if(count(ApiSubscriber::where('name', $name)->get()) > 0){
        return $this->Datareturn(false, 401, [], "name_reserved");
      }
      if(ctype_digit($name)){
        return $this->Datareturn(false, 401, [], "name_contains_only_numbers");
      }
      if(ctype_digit($name[0])){
        return $this->Datareturn(false, 401, [], "name_begin_with_number");
      }
      if(count(ApiSubscriber::where('email', $email)->get()) > 0){
        return $this->Datareturn(false, 401, [], "email_reserved");
      }
      if ($email == "" || $password == "" || $name == "" || $repassword == ""){
        return $this->Datareturn(false, 401, [], "same_parameter_empty");
      }
      if(strlen($password) < 6){
        return $this->Datareturn(false, 401, [], "password_to_short");
      }
      if(!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        return $this->Datareturn(false, 401, [], "its_not_email_format");
      }
      if(preg_match('/[\'^£$%&*()}{@#~?><>,!|=_+¬-]/', $name) || preg_match('/[\'^£$%&*()!}{@#~?><>,|=_+¬-]/', $password)){
        return $this->Datareturn(false, 401, [], "special_caracter_on_name_or_password");
      }
      if(strlen(trim($name)) == 0 || strlen(trim($password)) == 0){
        return $this->Datareturn(false, 401, [], "incorrect_name_or_password");
      }
      if($repassword != $password){
        return $this->Datareturn(false, 401, [], "password_and_repassword_are_not_match");
      }

      ApiSubscriber::create([
          'name' => $name,
          'email' => $email,
          'password' => $password,
          'report_number' => 0,
          'permission' => 0,
      ]);

      return $this->Datareturn(true, 200, [], "success_signup");
    }

    public function getUser(Request $request){
       try{
           $userid = $request->userid;
           if((!is_numeric($userid) && $userid != null) || $userid < 0 || $userid == "0"){
               return $this->Datareturn(false, 420, '', 'user_not_found');
           }
            if($userid != null) {
                $user = ApiSubscriber::where('users.user_id', $userid)->get();
                if(count($user) == 0 || $user[0]->user_id == null){
                    return $this->Datareturn(false, 420, '', 'user_not_found');
                }
            }else{
                $user = ApiSubscriber::where('users.user_id', $this->auth->user()->user_id)->get();
            }
           if(count($user)!= 0){
               foreach($user as $u){
                   $result = [
                           'user_id' => $u->user_id,
                           'name' => $u->name,
                           'email' => $u->email,
                           'report_number' => $u->report_number,
                           'created_at' => $u->created_at
                   ];
               }
               return $this->Datareturn(true, 200, $result, 'success_query');
           }else{
               return $this->Datareturn(false, 420, '', 'user_not_found');
           }
        }catch (TokenExpiredException $exception){
            return $this->Datareturn(false, 401, '', 'something_bad');
        }
    }

    public function passwordChange(Request $request){
        try {
          $new = $request->new;
          $confirm_new = $request->confirm_new;
          $old = $request->old;
          $userpw = ApiSubscriber::select('password')->where('user_id',$this->auth->user()->user_id)->first();
          if ($new == '' || $old == '' || $confirm_new == ''){
              return $this->Datareturn(false, 401, [], "same_parameter_empty");
          }
          if (!Hash::check($old, $userpw->password)){
              return $this->Datareturn(false, 401, [], 'password_not_equal');
          }
          if ($new == $old){
              return $this->Datareturn(false, 401, [], 'new_and_old_password_equal');
          }
          if(strlen($new) < 6){
              return $this->Datareturn(false, 401, [], "password_to_short");
          }
          if(preg_match('/[\'^£$%&*()!}{@#~?><>,|=_+¬-]/', $new)){
              return $this->Datareturn(false, 401, [], "special_caracter_on_name_or_password");
          }
          if(strlen(trim($new)) == 0){
              return $this->Datareturn(false, 401, [], "incorrect_name_or_password");
          }
          if($new != $confirm_new){
            return $this->Datareturn(false, 401, [], 'new_password_and_confirm_new_password_not_equal');
          }
          ApiSubscriber::where('user_id', $this->auth->user()->user_id)->update(['password' => Hash::make($new)]);
          return $this->Datareturn(true, 200, [], 'success_password_change');

        } catch (TokenExpiredException $e) {
            return $this->Datareturn(false, 401, '', 'something_bad');
        }

    }
}

<?php namespace App\Http\Controllers;

use Dingo\Api\Http\Request;
use Tymon\JWTAuth\JWTAuth;
use Tymon\JWTAuth\Exceptions\JWTException;
use App\Models\Report;
use App\Models\Image;
use Faker\Provider\File;
use Dingo\Api\Auth\Auth;
use Illuminate\Support\Facades\Input;
use Tymon\JWTAuth\Exceptions\TokenExpiredException;
use Intervention\Image\ImageManagerStatic as IMG;

class ReportController extends Controller
{
    protected $auth;

    public function __construct(JWTAuth $auth)
    {
        $this->auth = $auth;
    }

    public function getReport(Request $request){
      try {
          $reportid = $request->reportid;
          if($reportid == null){
            return $this->Datareturn(false, 401, '', 'reportid_is_null');
          }
          if(!is_numeric($reportid)){
            return $this->Datareturn(false, 401, '', 'reportid_is_not_numeric');
          }
          $report = Report::where('report_id', $reportid)->get();
          $pictures = Image::select('image_id', 'mini_image')->where('report_id', $reportid)->get();
          if(count($report) != 0){
            foreach ($report as $r) {
              $result[] = [
                  'report_id' => $r->report_id,
                  'latitude' => $r->latitude,
                  'longitude' => $r->longitude,
                  'description' => $r->description,
                  'mini_image' => $pictures
              ];
            }
            return $this->Datareturn(true, 200, $result, 'success_query');
          }else{
            return $this->Datareturn(false, 420, '', 'report_not_found');
          }

      } catch (TokenExpiredException $e) {
          return $this->Datareturn(false, 401, '', 'something_bad');
      }

    }

    public function newReport(Request $request){
      try {
        $latitude = $request->latitude;
        $longitude = $request->longitude;
        $description = $request->description;
        $picnumber = $request->picnumber;
        $pictures = [];

        if($latitude == null || $longitude == null || $description == null || $picnumber == null){
          return $this->Datareturn(false, 401, '', 'same_parameter_empty');
        }
        if(!doubleval($latitude)){
          return $this->Datareturn(false, 401, '', 'latitude_is_not_double');
        }
        if(!doubleval($longitude)){
          return $this->Datareturn(false, 401, '', 'longitude_is_not_double');
        }
        if(strlen($description) > 250){
          return $this->Datareturn(false, 401, '', 'description_is_too_long');
        }
        if(!is_numeric($picnumber)){
          return $this->Datareturn(false, 401, '', 'picnumber_is_not_numeric');
        }
        if(preg_match('/[\'^£$%&*()}{@#~?><>.,!|=_+¬-]/', $picnumber)){
          return $this->Datareturn(false, 401, '', 'picnumber_is_not_integer');
        }
        if($picnumber < 0 || $picnumber > 4){
          return $this->Datareturn(false, 401, '', 'picnumber_is_too_small_or_too_large');
        }

        Report::create([
          'user_id' => $this->auth->user()->user_id,
          'latitude' => $latitude,
          'longitude' => $longitude,
          'description' => $description
        ]);

        $reportid = Report::select('report_id')->orderBy('report_id', 'desc')->first();

        if($picnumber >= 1 && $request->picture1 != null){
            array_push($pictures, $request->picture1);
        }
        if($picnumber >= 2 && $request->picture2 != null){
            array_push($pictures, $request->picture2);
        }
        if($picnumber >= 3 && $request->picture3 != null){
            array_push($pictures, $request->picture3);
        }
        if($picnumber >= 4 && $request->picture4 != null){
            array_push($pictures, $request->picture4);
        }
        if(count($pictures) != 0){
          for ($i=0; $i < count($pictures); $i++) {
            $pic = $pictures[$i];
            $pic = base64_decode($pic);
            $chars = array(
                "á"=>"a","é"=>"e","í"=>"i",
                "ü"=>"u","ű"=>"u","ú"=>"u",
                "ő"=>"o","ö"=>"o","ó"=>"o",
                "Á"=>"A","É"=>"E","Í"=>"I",
                "Ü"=>"U","Ű"=>"U","Ú"=>"U",
                "Ő"=>"O","Ö"=>"O","Ó"=>"O"," "=>"_",
            );
            $thispicnum = $i;
            $thispicnum++;
            $name = $this->auth->user()->name;
            $name = str_replace(array_keys($chars), $chars, $name);
            $name = $name."_".$thispicnum."_".date('Y_m_d_H_i_s');
            $mininame = ('images/mini/'.$name.'.jpg');
            $bigname = ('images/normal/'.$name.'.jpg');
            $size = (array)getimagesizefromstring($pic);

            $image = IMG::canvas($size[0],$size[1],'ffffff');
            $image->insert($pic,'center')->save($bigname);

            $pic = IMG::make($bigname)->resize($size[0]*0.2,$size[1]*0.2);
            $pic->save($mininame);

            Image::create([
              'report_id' => $reportid->report_id,
              'image' => $bigname,
              'mini_image' => $mininame
            ]);
          }
        }

        return $this->Datareturn(true, 200, '', 'new_report_created_is_success');

      } catch (TokenExpiredException $e) {
          return $this->Datareturn(false, 401, '', 'something_bad');
      }

    }

    public function editReport(Request $request){
      //
    }

    public function deleteReport(Request $request){
      //
    }

}

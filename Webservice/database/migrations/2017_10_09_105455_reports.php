<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class Reports extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
      Schema::create('reports', function (Blueprint $table) {
          $table->increments('report_id');
          $table->integer('user_id')->unsigned();
          $table->double('latitude');
          $table->double('longitude');
          $table->string('description');
          $table->timestamps();

          $table->foreign('user_id')->references('user_id')->on('users');
      });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        //
    }
}

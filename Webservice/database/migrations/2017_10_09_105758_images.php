<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class Images extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
      Schema::create('images', function (Blueprint $table) {
          $table->increments('image_id');
          $table->integer('report_id')->unsigned();
          $table->string('image');
          $table->string('mini_image');
          $table->timestamps();

          $table->foreign('report_id')->references('report_id')->on('reports');

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

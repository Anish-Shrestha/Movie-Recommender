
import java.util.Random

import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.mllib.recommendation.{ ALS, MatrixFactorizationModel, Rating }
import org.apache.spark.rdd._
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.hive.HiveContext;
import scala.io.Source;
object MovieLensALS {

  def main(args: Array[String]) {

    // set up environment

    val conf = new SparkConf().setAppName("Recommendation App").setMaster("local")

    val sc = new SparkContext(conf)

    
    val movieLensHomeDir = args(0)

  val ratings = sc.textFile(movieLensHomeDir + "/ratings.csv").map { line =>
      val fields = line.split(",")
      // format: (timestamp % 10, Rating(userId, movieId, rating))
      (fields(3).toLong % 10, Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble))
    }
   

    val movies = sc.textFile(movieLensHomeDir + "/movies.csv").map { line =>
      val fields = line.split(",")
      // format: (movieId, movieName)
      (fields(0).toInt, fields(1))
    }.collect.toMap


    val mostRatedMovieIds = ratings.map(_._2.product) // extract movie ids
      .countByValue // count ratings per movie
      .toSeq // convert map to Seq
      .sortBy(-_._2) // sort by rating count
      .take(50) // take 50 most rated
      .map(_._1) // get their ids
      
    val random = new Random(0)
    val selectedMovies = mostRatedMovieIds.filter(x => random.nextDouble() < 0.2)
      .map(x => (x, movies(x)))
      .toSeq
 //	var lines1 = sc.textFile(movieLensHomeDir + "/"+args(1)+".csv")
 		var lines1 = sc.textFile(movieLensHomeDir + "/ratings.csv")
    val myRatings = loadRatings(lines1,args(1).toInt)
    // elicitateRatings(selectedMovies)
    val myRatingsRDD = sc.parallelize(myRatings)
    val numPartitions = 20
    val training = ratings.filter(x => x._1 < 6)
      .values
      .union(myRatingsRDD)
      .repartition(numPartitions)
      .persist
    val validation = ratings.filter(x => x._1 >= 6 && x._1 < 8)
      .values
      .repartition(numPartitions)
      .persist
    val test = ratings.filter(x => x._1 >= 8).values.persist

    val numTraining = training.count
    val numValidation = validation.count
    val numTest = test.count

    println("Training: " + numTraining + ", validation: " + numValidation + ", test: " + numTest)
    val ranks = List(8, 12)
    val lambdas = List(0.1, 10.0)
    val numIters = List(10, 20)
    var bestModel: Option[MatrixFactorizationModel] = None
    var bestValidationRmse = Double.MaxValue
    var bestRank = 0
    var bestLambda = -1.0
    var bestNumIter = -1
    for (rank <- ranks; lambda <- lambdas; numIter <- numIters) {
      val model = ALS.train(training, rank, numIter, lambda)
      val validationRmse = computeRmse(model, validation, numValidation)
      println("RMSE (validation) = " + validationRmse + " for the model trained with rank = "
        + rank + ", lambda = " + lambda + ", and numIter = " + numIter + ".")
      if (validationRmse < bestValidationRmse) {
        bestModel = Some(model)
        bestValidationRmse = validationRmse
        bestRank = rank
        bestLambda = lambda
        bestNumIter = numIter
      }

    }

    val testRmse = computeRmse(bestModel.get, test, numTest)
	
    println("The best model was trained with rank = " + bestRank + " and lambda = " + bestLambda
      + ", and numIter = " + bestNumIter + ", and its RMSE on the test set is " + testRmse + ".")

    val myRatedMovieIds = myRatings.map(_.product).toSet
    val candidates = sc.parallelize(movies.keys.filter(!myRatedMovieIds.contains(_)).toSeq)
    val recommendations = bestModel.get
      .predict(candidates.map((0, _)))
      .collect
      .sortBy(-_.rating)
      .take(50)

    var i = 1
    
     
     
    var z = new Array[String](50)  
   
    recommendations.foreach { r =>
    //  println("%2d".format(i) + ": " + movies(r.product))
       z(i-1) = "%2d".format(i) + ": " + movies(r.product);
      i += 1
    }
  
	var recomendationRDD = sc.parallelize(z)
	
	recomendationRDD.saveAsTextFile(args(2))
    
    // clean up
    sc.stop()
  }

  /** Compute RMSE (Root Mean Squared Error). */
  def computeRmse(model: MatrixFactorizationModel, data: RDD[Rating], n: Long) = {
    val predictions: RDD[Rating] = model.predict(data.map(x => (x.user, x.product)))
    val predictionsAndRatings = predictions.map(x => ((x.user, x.product), x.rating))
      .join(data.map(x => ((x.user, x.product), x.rating)))
      .values
    math.sqrt(predictionsAndRatings.map(x => (x._1 - x._2) * (x._1 - x._2)).reduce(_ + _) / n)
  }

    /** Load ratings from file. */
  def loadRatings(lines: RDD[String],userid: Int): Seq[Rating] = {
  //   val ratings = lines.collect().map { line =>
   //   val fields = line.split(",")
   //        Rating(0, fields(1).toInt, fields(2).toDouble)
   // }.filter(_.rating > 0.0)
    
  val ratings = lines.collect().map { line =>
     val fields = line.split(",")
     (fields(3).toLong % 10, fields(0).toInt, fields(1).toInt, fields(2).toDouble)
    }.filter(f=>f._2==userid).map(f=> (Rating(0, f._2.toInt, f._3.toDouble))).filter(_.rating > 0.0)
    
    if (ratings.isEmpty) {
      sys.error("No ratings provided.")
    } else {
      ratings.toSeq
    }
  }
  
  /** Elicitate ratings from command-line. */
  def elicitateRatings(movies: Seq[(Int, String)]) = {
    val prompt = "Please rate the following movie (1-5 (best), or 0 if not seen):"
    println(prompt)
    val ratings = movies.flatMap { x =>
      var rating: Option[Rating] = None
      var valid = false
      while (!valid) {
        print(x._2 + ": ")
        try {
          val r = Console.readInt
          if (r < 0 || r > 5) {
            println(prompt)
          } else {
            valid = true
            if (r > 0) {
              rating = Some(Rating(0, x._1, r))
            }
          }
        } catch {
          case e: Exception => println(prompt)
        }
      }
      rating match {
        case Some(r) => Iterator(r)
        case None    => Iterator.empty
      }
    }
    if (ratings.isEmpty) {
      error("No rating provided!")
    } else {
      ratings
    }
  }
}
package main

import (
	"flag"
	"fmt"
	"log"
	"math/rand"
	"os"
	"sync"
	"time"
)

const (
	timeUpperLevel = "2021-04-18 13:42:23"
	timeLowerLevel = "2020-11-23 09:15:09"

	numData = 10000

	numUsers = 1000
	numNews  = 100
	numTypes = 3

	path = "./inputs"
)

func main() {
	timeUp := flag.String("tu", timeUpperLevel, "upper time level")
	timeLow := flag.String("tl", timeLowerLevel, "lower time level")

	numD := flag.Int("nd", numData, "number of data (rows)")

	numU := flag.Int("u", numUsers, "number of users")
	numN := flag.Int("n", numNews, "number of news")

	pathDest := flag.String("path", path, "destination directory, select a special directory for files, "+
		"so before creating new files, all the old ones will be deleted")

	flag.Parse()

	timeU, err := time.Parse("2006-01-02 15:04:05", *timeUp)
	if err != nil {
		log.Fatalln("incorrect date format")
	}

	timeL, err := time.Parse("2006-01-02 15:04:05", *timeLow)
	if err != nil {
		log.Fatalln("incorrect date format")
	}

	if timeU.Before(timeL) {
		log.Fatalln("incorrect dates")
	}

	if *numD < 100 {
		log.Fatalln("incorrect number of data (rows) (minimum = 100)")
	}

	if *numU < 1 {
		log.Fatalln("incorrect number of users")
	}

	if *numN < 1 {
		log.Fatalln("incorrect number of users")
	}

	dirStat, err := os.Stat(*pathDest)
	if err != nil {
		log.Fatalln("incorrect path")
	}
	if !dirStat.IsDir() {
		log.Fatalln("no directory entered")
	}

	err = os.RemoveAll(*pathDest)
	if err != nil {
		log.Fatalln("unable to remove files from a directory")
	}

	err = os.Mkdir(*pathDest, os.FileMode(0775))
	if err != nil {
		log.Fatalln("unable to make dir")
	}

	wg := &sync.WaitGroup{}

	wg.Add(1)
	go func(wg *sync.WaitGroup) {
		err = createFileTypes(*pathDest)
		if err != nil {
			log.Fatalln("unable to create file screen")
		}
		wg.Done()
	}(wg)

	err = os.Mkdir(*pathDest+string(os.PathSeparator)+"interactions", os.FileMode(0775))
	if err != nil {
		log.Fatalln("unable to make dir")
	}

	wg.Add(1)
	go func(wg *sync.WaitGroup) {
		err = createFileInteractions(*pathDest+string(os.PathSeparator)+"interactions", *numD, *numU,
			*numN, timeL.Unix(), timeU.Unix())
		if err != nil {
			log.Fatalln("unable to create interactions file")
		}
		wg.Done()
	}(wg)

	wg.Wait()
}

func createFileTypes(path string) error {
	path += string(os.PathSeparator) + "types"
	err := os.Mkdir(path, os.FileMode(0775))
	if err != nil {
		log.Fatalln("unable to make dir")
	}

	file, err := os.Create(path + string(os.PathSeparator) + "types")
	if err != nil {
		return err
	}
	defer func() {
		err = file.Close()
		if err != nil {
			log.Fatalln("unable to close file")
		}
	}()
	_, err = fmt.Fprintf(file, "%d-%s\n", 1, "открыл и прочитал")
	if err != nil {
		return err
	}
	_, err = fmt.Fprintf(file, "%d-%s\n", 2, "открыл на предпросмотр")
	if err != nil {
		return err
	}
	_, err = fmt.Fprintf(file, "%d-%s\n", 3, "не взаимодействовал")
	if err != nil {
		return err
	}

	return nil
}

func createFileInteractions(path string, numRows, numUsers, numNews int, lowTime, upTime int64) error {
	file, err := os.Create(fmt.Sprintf("%s%s%s", path, string(os.PathSeparator), "interactions"))
	if err != nil {
		return err
	}
	defer func() {
		err = file.Close()
		if err != nil {
			log.Fatalln("unable to close file")
		}
	}()

	for i := 0; i < numRows; i++ {
		userId := rand.Intn(numUsers) + 1
		newsId := rand.Intn(numNews) + 1
		typeId := rand.Intn(numTypes) + 1
		tim := time.Unix(rand.Int63n(upTime-lowTime)+lowTime, 0).Format("2006-01-02 15:04:05")

		_, err = file.WriteString(fmt.Sprintf("%d,%d,%s,%d\n", newsId, userId, tim, typeId))
		if err != nil {
			return err
		}
	}

	return nil
}
